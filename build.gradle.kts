plugins {
    base
    alias(libs.plugins.fabric.loom) apply false
}

group = "dev.denimred.littlethings"
base.archivesName.set("littlethings")
