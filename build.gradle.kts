plugins {
    id("java-library")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

group = "dev.denimred"
version = "0.1.0"
base.archivesName = "littlethings"

minecraft { version("1.18.2") }

tasks.jar { from("LICENSE") }