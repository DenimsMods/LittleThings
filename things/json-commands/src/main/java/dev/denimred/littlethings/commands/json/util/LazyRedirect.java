package dev.denimred.littlethings.commands.json.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommand;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

/**
 * Lazily finds the target of a command redirect.
 * Used to allow command nodes to be built without needing their redirect targets to exist yet.
 *
 * @param <S> the command source type.
 */
public final class LazyRedirect<S> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final CommandNode<S> node;
    private final CommandDispatcher<S> dispatcher;
    private final @Path String targetPath;

    private @Nullable CommandNode<S> redirect = null;
    private boolean complained = false;

    LazyRedirect(CommandNode<S> node, CommandDispatcher<S> dispatcher, @Path String targetPath) {
        this.node = node;
        this.dispatcher = dispatcher;
        this.targetPath = targetPath;
    }

    /**
     * Wraps a command node in one that lazily computes its redirect target.
     * The returned node will match the input node's type as its common for the command system to use instanceof checks.
     *
     * @param node the node to wrap.
     * @param dispatcher the dispatcher containing the redirect target node.
     * @param targetPath the path to the redirect target node.
     * @param <S> the command source type.
     *
     * @return a wrapped node with its redirect being lazily computed.
     */
    public static <S> CommandNode<S> wrap(CommandNode<S> node, CommandDispatcher<S> dispatcher, @Path String targetPath) {
        if (node instanceof LiteralCommandNode<S> literal) return new Literal<>(literal, dispatcher, targetPath);
        if (node instanceof ArgumentCommandNode<S, ?> argument) return new Argument<>(argument, dispatcher, targetPath);
        if (node instanceof RootCommandNode<S> root) return root; // Cannot have a redirect
        throw new IllegalArgumentException("Unknown node type " + node.getClass());
    }

    @Nullable CommandNode<S> resolve() {
        if (redirect == null) redirect = dispatcher.findNode(List.of(targetPath.split("/")));
        if (redirect == null && !complained) {
            LOGGER.warn("Missing redirect target {} -> {}", JsonCommand.path(dispatcher.getPath(node)), targetPath);
            complained = true;
        }
        return redirect;
    }
}

final class Literal<S> extends LiteralCommandNode<S> {
    private final LazyRedirect<S> lazyRedirect;

    Literal(LiteralCommandNode<S> literal, CommandDispatcher<S> dispatcher, @Path String targetPath) {
        super(literal.getLiteral(), literal.getCommand(), literal.getRequirement(), null, literal.getRedirectModifier(), literal.isFork());
        this.lazyRedirect = new LazyRedirect<>(this, dispatcher, targetPath);
    }

    @Override
    public @Nullable CommandNode<S> getRedirect() {
        return lazyRedirect.resolve();
    }
}

final class Argument<S, T> extends ArgumentCommandNode<S, T> {
    private final LazyRedirect<S> lazyRedirect;

    Argument(ArgumentCommandNode<S, T> argument, CommandDispatcher<S> dispatcher, @Path String targetPath) {
        super(argument.getName(), argument.getType(), argument.getCommand(), argument.getRequirement(), null, argument.getRedirectModifier(), argument.isFork(), argument.getCustomSuggestions());
        this.lazyRedirect = new LazyRedirect<>(this, dispatcher, targetPath);
    }

    @Override
    public @Nullable CommandNode<S> getRedirect() {
        return lazyRedirect.resolve();
    }
}
