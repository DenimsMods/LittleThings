plugins {
    id("littlethings.conventions")
    kotlin("jvm") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
}

version = "0.1.0"

dependencies {
    ksp(project(":$name:processor"))
}

ksp {
    arg("output", "dev.denimred.littlethings.assertions.LittleAssertions")
    arg("functions", "byte|short|int|long|float|double|char")
    arg("assert-require", "java.lang.IllegalArgumentException")
    arg("assert-check", "java.lang.IllegalStateException")
}
