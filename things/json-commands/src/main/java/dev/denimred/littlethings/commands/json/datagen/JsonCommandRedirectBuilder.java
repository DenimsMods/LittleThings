package dev.denimred.littlethings.commands.json.datagen;

import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommand;
import dev.denimred.littlethings.commands.json.JsonCommandRedirect;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/** A simple builder abstraction over {@link JsonCommandRedirect}. */
public class JsonCommandRedirectBuilder {
    protected final @Path String target;
    protected final @Subst("parent") JsonCommandBuilder parent;
    protected @Nullable ResourceLocation modifier;
    protected @Nullable Boolean forks;

    /**
     * Constructs a new JSON command redirect builder.
     *
     * @param target the redirect target path.
     * @param parent the command this redirect is owned by.
     */
    public JsonCommandRedirectBuilder(@Path String target, JsonCommandBuilder parent) {
        this.target = target;
        this.parent = parent;
    }

    /**
     * Specifies the redirect modifier of this command redirect.
     *
     * @param modifier the ID of the redirect modifier to associate with.
     *
     * @return the containing redirect builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandRedirectBuilder modifier(@Nullable ResourceLocation modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Specifies the redirect modifier of this command redirect.
     *
     * @param modifier the path half of the modifier's ID. The parent command's namespace will be used for the other half.
     *
     * @return the containing redirect builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandRedirectBuilder modifier(@Path String modifier) {
        return modifier(new ResourceLocation(parent.getNamespace(), modifier));
    }

    /**
     * Specifies the redirect modifier of this command redirect.
     * Will use the parent command's namespace and path as the modifier ID.
     *
     * @return the containing redirect builder (this).
     */
    @Contract("-> this")
    public JsonCommandRedirectBuilder modifier() {
        return modifier(parent.getPath());
    }

    /**
     * Specifies that this redirect forks.
     * Affects command execution handling and integer returns.
     *
     * @return the containing redirect builder (this).
     */
    @Contract("-> this")
    public JsonCommandRedirectBuilder forks() {
        forks = true;
        return this;
    }

    /**
     * Retrieves the parent of this redirect builder, if applicable.
     *
     * @return the parent of this builder.
     */
    @Contract(pure = true)
    public JsonCommandBuilder pop() {
        return parent;
    }

    /**
     * Constructs a new {@link JsonCommandRedirect} using the data provided by this builder.
     *
     * @return the assembled JSON command redirect.
     */
    @Contract(value = "-> new", pure = true)
    public JsonCommandRedirect assemble() {
        return new JsonCommandRedirect(target, modifier, forks);
    }
}
