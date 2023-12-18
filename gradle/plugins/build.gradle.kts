plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    libs.versions.apply {
        implementation("net.fabricmc:fabric-loom:" + fabric.loom.get())
    }
}
