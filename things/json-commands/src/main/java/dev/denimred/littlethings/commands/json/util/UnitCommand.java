package dev.denimred.littlethings.commands.json.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

/**
 * Wrapper over {@link Command} that lets you define commands without requiring the integer return.
 * @param <S> the source type, usually {@link CommandSourceStack}.
 */
@FunctionalInterface
public interface UnitCommand<S> extends Command<S> {
    /**
     * The command execution code.
     * @param ctx the context containing everything needed for a command to read its arguments and interact with the source.
     * @throws CommandSyntaxException if a command exception occurs.
     */
    void runUnit(CommandContext<S> ctx) throws CommandSyntaxException;

    @Override
    default int run(CommandContext<S> ctx) throws CommandSyntaxException {
        runUnit(ctx);
        return SINGLE_SUCCESS;
    }
}
