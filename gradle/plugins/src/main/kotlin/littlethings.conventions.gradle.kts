@file:Suppress("UnstableApiUsage")

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    `java-library`
    `maven-publish`
    id("fabric-loom")
    idea
}

group = rootProject.group
base.archivesName.set(rootProject.base.archivesName.map { "${it}-${project.name}" })

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
    jar { from("LICENSE") }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 17
    }

    withType<AbstractArchiveTask> {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
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
