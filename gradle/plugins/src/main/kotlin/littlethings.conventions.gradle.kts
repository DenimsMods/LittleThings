@file:Suppress("UnstableApiUsage")

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    `java-library`
    `maven-publish`
    id("fabric-loom")
    `version-catalog`
    idea
}

group = rootProject.group
base.archivesName.set(project.name)

loom {

}

inline val self get() = project
rootProject.tasks.named("generateCatalogAsToml") {
    rootProject.catalog {
        versionCatalog {
            version(self.name, self.version.toString())
            library(self.name, self.group.toString(), self.base.archivesName.get()).versionRef(self.name)
        }
    }
}

if (project.name != "annotations") {
    dependencies {
        compileOnly(project(":annotations"))
        testCompileOnly(project(":annotations"))
    }
}

repositories {
    maven("https://maven.parchmentmc.net/") {
        name = "ParchmentMC (Mappings)"
        content { includeGroup(libs.parchment.orNull?.group!!) }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(libs.parchment) { artifactType("zip") })
    })
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test { useJUnitPlatform() }

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = sourceCompatibility
    withSourcesJar()
}

tasks {
    jar {
        manifest { attributes["Fabric-Loom-Remap"] = true }
        from("LICENSE")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 17
    }

    withType<AbstractArchiveTask> {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }

    processResources {
        inputs.property("version", version)
        filesMatching("fabric.mod.json") { expand("version" to version) }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories { mavenLocal() }
}
