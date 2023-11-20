plugins {
    id("java-library")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    id("maven-publish")
}

group = "dev.denimred"
version = "0.3.1"
base.archivesName = "littlethings"

minecraft { version("1.18.2") }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test { useJUnitPlatform() }

tasks.jar { from("LICENSE") }

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 17
}

java {
    withJavadocJar()
    withSourcesJar()
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