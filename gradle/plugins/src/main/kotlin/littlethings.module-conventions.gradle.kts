@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    `java-library`
    `maven-publish`
    id("fabric-loom")
    idea
}

group = rootProject.group
base.archivesName.set(rootProject.base.archivesName.map { "${it}-${project.name}" })

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
}

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

val testmod by sourceSets.registering {
    compileClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    resources { srcDir("src/testmod/generated") }
}

loom {
    mods.register(project.name + "-testmod") { sourceSet(testmod.get()) }
    createRemapConfigurations(testmod.get())
    runs {
        delete("client")
        delete("server")
        register("testmodClient") {
            client()
            source(testmod.get())
            name(project.name.uppercaseFirstChar() + ": Client")
            ideConfigGenerated(true)
            runDir("run/client")
        }
        register("testmodServer") {
            server()
            source(testmod.get())
            name(project.name.uppercaseFirstChar() + ": Server")
            ideConfigGenerated(true)
            runDir("run/server")
        }
        register("testmodDatagen") {
            inherit(getByName("testmodClient"))
            name(project.name.uppercaseFirstChar() + ": Datagen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/testmod/generated")}")
            vmArg("-Dfabric-api.datagen.modid=testmod")
            vmArg("-Dfabric-api.datagen.strict-validation")
            runDir("build/datagen")
        }
    }
}

tasks.named("ideaSyncTask") {
    doLast {
        // This is stupid and hacky, but I don't want loom to append the run config name with the project name
        rootProject.file(".idea").resolve("runConfigurations").listFiles()?.forEach { file ->
            if (file.extension != "xml") return@forEach
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            with(doc.documentElement) {
                val config = getElementsByTagName("configuration").item(0) as Element
                config.setAttribute("name", config.getAttribute("name").replace(" \\(:.*\\)".toRegex(), ""))
            }
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            transformer.transform(DOMSource(doc), StreamResult(file))
        }
    }
}

dependencies {
    if (project.name != "annotations") {
        compileOnly(project(":annotations"))
        testCompileOnly(project(":annotations"))
        "testmodCompileOnly"(project(":annotations"))
    }
    "modTestmodImplementation"(libs.fabric.api)
    "modTestmodImplementation"(libs.fabric.loader)
}

idea {
    module {
        testSources.from(testmod.get().allJava.srcDirs)
        testResources.from(testmod.get().resources.srcDirs)
    }
}
