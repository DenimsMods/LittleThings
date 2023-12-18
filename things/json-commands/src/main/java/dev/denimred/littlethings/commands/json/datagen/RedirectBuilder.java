package dev.denimred.littlethings.commands.json.datagen;

import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommandRedirect;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class RedirectBuilder {
    protected final @Path String target;
    protected final @Subst("parent") CommandBuilder parent;
    protected @Nullable ResourceLocation modifier;
    protected @Nullable Boolean forks;

    public RedirectBuilder(@Path String target, CommandBuilder parent) {
        this.target = target;
        this.parent = parent;
    }

    @Contract("_ -> this")
    public RedirectBuilder modifier(@Nullable ResourceLocation modifier) {
        this.modifier = modifier;
        return this;
    }

    @Contract("_ -> this")
    public RedirectBuilder modifier(@Path String modifier) {
        return modifier(new ResourceLocation(parent.getProvider().getNamespace(), modifier));
    }

    @Contract("-> this")
    public RedirectBuilder modifier() {
        return modifier(parent.getPath());
    }

    @Contract("-> this")
    public RedirectBuilder forks() {
        forks = true;
        return this;
    }

    @Contract(pure = true)
    public CommandBuilder pop() {
        return parent;
    }

    @Contract(value = "-> new", pure = true)
    public JsonCommandRedirect assemble() {
        return new JsonCommandRedirect(target, modifier, forks);
    }
}
