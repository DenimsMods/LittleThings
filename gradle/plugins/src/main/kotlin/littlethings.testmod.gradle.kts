
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

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
            appendProjectPathToConfigName.set(false)
            name(project.name.uppercaseFirstChar() + ": Client")
            ideConfigGenerated(true)
            runDir("run/client")
        }
        register("testmodServer") {
            server()
            source(testmod.get())
            appendProjectPathToConfigName.set(false)
            name(project.name.uppercaseFirstChar() + ": Server")
            ideConfigGenerated(true)
            runDir("run/server")
        }
        register("testmodDatagen") {
            inherit(getByName("testmodClient"))
            appendProjectPathToConfigName.set(false)
            name(project.name.uppercaseFirstChar() + ": Datagen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/testmod/generated")}")
            vmArg("-Dfabric-api.datagen.modid=testmod")
            vmArg("-Dfabric-api.datagen.strict-validation")
            runDir("build/datagen")
        }
    }
}

dependencies {
    "modTestmodImplementation"(libs.fabric.api)
    "modTestmodImplementation"(libs.fabric.loader)
    "testmodCompileOnly"(project(":annotations"))
}
