plugins {
    id("littlethings.conventions")
    id("littlethings.testmod")
    alias(libs.plugins.kotlin.jvm)
}

version = "0.1.0-SNAPSHOT"

kotlin { explicitApi() }
