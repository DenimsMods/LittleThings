package dev.denimred.littlethings.testmod

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import dev.denimred.littlethings.commands.json.JsonCommandManager
import dev.denimred.littlethings.commands.json.util.UnitCommand
import dev.denimred.littlethings.testmod.JsonCommandsTest.ID
import dev.denimred.littlethings.testmod.JsonCommandsTest.res
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.packs.PackType.SERVER_DATA

object JsonCommandsDslTest {
    private val commands = JsonCommandManager("$ID-dsl") {
        setExecutable("json_test/literal_child", UnitCommand { ctx ->
            ctx.source.sendSuccess(TextComponent("Good job!"), false)
        })
        setExecutable("json_test/integer_child", Command { ctx ->
            val i = IntegerArgumentType.getInteger(ctx, "int")
            ctx.source.sendSuccess(TextComponent("Good job x$i!"), false)
            i
        })
    }

    @JvmStatic
    fun init() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _ -> commands.setDispatcher(dispatcher) }
        val listener = DelegatedResourceReloadListener(res("json_commands"), commands)
        ResourceManagerHelper.get(SERVER_DATA).registerReloadListener(listener)
    }
}
