
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
    id("fabric-loom")
    idea
}

val testmod by sourceSets.registering {
    compileClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    resources { srcDir("src/testmod/generated") }
}

idea {
    module {
        testSources.from(testmod.get().allJava.srcDirs)
        testResources.from(testmod.get().resources.srcDirs)
    }
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
    "modTestmodImplementation"(libs.fabric.api)
    "modTestmodImplementation"(libs.fabric.loader)
    "testmodCompileOnly"(project(":annotations"))
}
