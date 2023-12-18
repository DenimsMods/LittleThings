plugins { id("littlethings.module-conventions") }

version = "0.1.0-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test { useJUnitPlatform() }
