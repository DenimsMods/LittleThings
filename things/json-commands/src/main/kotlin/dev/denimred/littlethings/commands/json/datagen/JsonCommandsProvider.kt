package dev.denimred.littlethings.commands.json.datagen

import dev.denimred.littlethings.annotations.Resource.Path
import net.minecraft.data.DataGenerator

public inline fun JsonCommandProvider(
    generator: DataGenerator, namespace: String, crossinline setup: JsonCommandProvider.() -> Unit
): JsonCommandProvider = object : JsonCommandProvider(generator, namespace) {
    override fun generateCommands() = setup(this)
}

public inline fun JsonCommandProvider.command(
    @Path name: String, action: JsonCommandBuilder.() -> Unit
) {
    add(command(name).apply(action))
}

public inline fun JsonCommandProvider.alias(
    @Path name: String, @Path target: String, action: JsonCommandBuilder.() -> Unit
) {
    add(alias(name, target).apply(action))
}

public inline fun JsonCommandBuilder.argument(
    @Path name: String, action: JsonCommandBuilder.() -> Unit
) {
    argument(name).apply(action)
}

public inline fun JsonCommandBuilder.redirect(
    @Path target: String, action: JsonCommandRedirectBuilder.() -> Unit
) {
    redirect(target).apply(action)
}
