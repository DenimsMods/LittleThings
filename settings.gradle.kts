pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "littlethings"

includeModule("annotations")
includeModule("facets")
includeModule("json-commands")
includeModule("todo")

fun includeModule(vararg modules: String) = modules.forEach { module ->
    include(module)
    project(":$module").apply {
        projectDir = file("things/$name").apply { mkdirs() }
        buildFileName = "$name.gradle.kts"
        if (buildFile.createNewFile()) {
            buildFile.writeText("plugins { id(\"littlethings.module-conventions\") }\n\nversion = \"0.1.0-SNAPSHOT\"\n")
            projectDir.resolve("src/main/java/dev/denimred/littlethings/$name").mkdirs()
            projectDir.resolve("README.md").createNewFile()
        }
    }
}
