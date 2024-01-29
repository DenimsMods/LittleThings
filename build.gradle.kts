plugins {
    base
    alias(libs.plugins.fabric.loom) apply false
    `version-catalog`
    `maven-publish`
}

group = "dev.denimred.littlethings"
base.archivesName.set("littlethings")

tasks.named("generateCatalogAsToml") {
    catalog {
        versionCatalog {
            bundle("dev", listOf("annotations", "todo"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("catalog") {
            groupId = group.toString()
            artifactId = base.archivesName.get() + "-catalog"
            version = libs.versions.minecraft.get()
            from(components["versionCatalog"])
        }
    }
}
