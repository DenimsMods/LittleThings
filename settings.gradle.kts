pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "littlethings"

thing("annotations")
thing("assertions", "processor")
thing("facets")
thing("json-commands")
thing("todo")

fun thing(module: String, vararg subModules: String) {
    include(module)
    project(":$module").apply {
        projectDir = file("things/$name").apply { mkdirs() }
        buildFileName = "$name.gradle.kts"
        if (buildFile.createNewFile()) {
            buildFile.writeText("plugins { id(\"littlethings.conventions\") }\n\nversion = \"0.1.0\"\n")
            projectDir.resolve("src/main/java/dev/denimred/littlethings/$name").mkdirs()
            val readme = projectDir.resolve("README.md")
            if (readme.createNewFile()) readme.writeText("# $name\n\n[//]: # (TODO: Describe the $name module)")
        }
    }
    subModules.forEach {
        include("$module:$it")
        project(":$module:$it").apply {
            projectDir = file("things/$module/$name").apply { mkdirs() }
            buildFileName = "$name.gradle.kts"
            buildFile.createNewFile()
        }
    }
}
