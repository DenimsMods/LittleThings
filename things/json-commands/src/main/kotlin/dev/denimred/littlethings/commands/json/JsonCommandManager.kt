package dev.denimred.littlethings.commands.json

import com.google.gson.Gson
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.commands.CommandSourceStack

public typealias Source = CommandSourceStack
public typealias Dispatcher = CommandDispatcher<Source>
public typealias LiteralArg = LiteralArgumentBuilder<Source>
public typealias LiteralNode = LiteralCommandNode<Source>
public typealias Context = CommandContext<Source>
public typealias Arg = ArgumentBuilder<Source, *>

private typealias ManagerSetup = JsonCommandManager.() -> Unit

public inline fun JsonCommandManager(namespace: String, setup: ManagerSetup): JsonCommandManager =
    JsonCommandManager(namespace).apply(setup)

public inline fun JsonCommandManager(namespace: String, gson: Gson, setup: ManagerSetup): JsonCommandManager =
    JsonCommandManager(namespace, gson).apply(setup)
