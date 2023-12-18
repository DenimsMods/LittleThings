plugins {
    id("littlethings.module-conventions")
    alias(libs.plugins.kotlin.jvm)
}

version = "0.1.0-SNAPSHOT"

kotlin { explicitApi() }
