package dev.denimred.littlethings.testmod

import dev.denimred.littlethings.commands.json.datagen.*
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.commands.Commands

object JsonCommandsDatagenDslTest {
    @JvmStatic
    fun init(generator: FabricDataGenerator) {
        // Fabric's data generator doesn't cooperate well with multiple entrypoints, so we run it manually here
        JsonCommandsProviderTest().onInitializeDataGenerator(generator)

        generator.addProvider(JsonCommandProvider(generator, generator.modId + "-dsl") {
            command("json_test") {
                argument("literal_child") { executable() }
                argument("integer_child") {
                    level(Commands.LEVEL_ADMINS)
                    executable()
                    type("brigadier:integer")
                    parameter("min", 0)
                    parameter("max", 100)
                }
            }
            alias("alias_test", "json_test") {}
            command("triple_literal") {
                redirect("json_test/literal_child") {
                    modifier()
                    forks()
                }
            }
        })
    }
}

