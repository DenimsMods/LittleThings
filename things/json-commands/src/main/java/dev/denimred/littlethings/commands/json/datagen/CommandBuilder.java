package dev.denimred.littlethings.commands.json.datagen;

import com.google.gson.JsonObject;
import dev.denimred.littlethings.annotations.Resource;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommand;
import dev.denimred.littlethings.commands.json.util.ArgumentDeserializer;
import dev.denimred.littlethings.commands.json.util.LevelString;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandBuilder {
    protected final JsonCommandProvider provider;
    protected final @Nullable CommandBuilder parent;

    protected final @Path String name;
    protected final @Path String path;
    protected final List<CommandBuilder> arguments = new ArrayList<>();
    protected @Nullable ResourceLocation type = null;
    protected @Nullable JsonObject parameters = null;
    protected @Nullable Integer level = null;
    protected @Nullable ResourceLocation executable = null;
    protected @Nullable RedirectBuilder redirect = null;

    public CommandBuilder(JsonCommandProvider provider, @Subst("parent") @Nullable CommandBuilder parent, @Path String name) {
        this.provider = provider;
        this.parent = parent;
        this.name = name;
        this.path = parent != null ? parent.path + "/" + name : name;
    }

    @Contract("_ -> this")
    public CommandBuilder type(ResourceLocation type) {
        this.type = type;
        return this;
    }

    @Contract("_ -> this")
    public CommandBuilder type(@Resource String type) {
        return type(new ResourceLocation(type));
    }

    @Contract("_ -> this")
    public CommandBuilder type(ArgumentDeserializer.Named deserializer) {
        return type(deserializer.typeId());
    }

    @Contract("_, _ -> this")
    public CommandBuilder parameter(String name, boolean value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    @Contract("_, _ -> this")
    public CommandBuilder parameter(String name, Number value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    @Contract("_, _ -> this")
    public CommandBuilder parameter(String name, String value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    @Contract("_, _ -> this")
    public CommandBuilder parameter(String name, Character value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    @Contract("_ -> this")
    public CommandBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Contract("_ -> this")
    public CommandBuilder level(@LevelString String level) {
        return level(JsonCommand.levelValue(level));
    }

    @Contract("_ -> this")
    public CommandBuilder executable(ResourceLocation executable) {
        this.executable = executable;
        return this;
    }

    @Contract("_ -> this")
    public CommandBuilder executable(@Path String executable) {
        return executable(new ResourceLocation(provider.getNamespace(), executable));
    }

    @Contract("-> this")
    public CommandBuilder executable() {
        return executable(path);
    }

    @Contract("_ -> new")
    public CommandBuilder argument(@Path String name) {
        var arg = new CommandBuilder(provider, this, name);
        arguments.add(arg);
        return arg;
    }

    @Contract("_ -> new")
    public RedirectBuilder redirect(@Path String target) {
        return redirect = new RedirectBuilder(target, this);
    }

    @Contract("_ -> new")
    public RedirectBuilder redirect(@Subst("target/path") String... target) {
        return redirect(JsonCommand.path(target));
    }

    @Contract(pure = true)
    public CommandBuilder pop() {
        return Objects.requireNonNull(parent, "Cannot get parent of root");
    }

    @Contract(value = "-> new", pure = true)
    public JsonCommand assemble() {
        var size = arguments.size();
        var children = new JsonCommand[size];
        for (int i = 0; i < size; i++) children[i] = this.arguments.get(i).assemble();
        var assembledRedirect = redirect != null ? redirect.assemble() : null;
        return new JsonCommand(name, type, parameters, level, executable, assembledRedirect, path, children);
    }

    @Contract(pure = true)
    public final boolean isChild() {
        return parent != null;
    }

    @Contract(pure = true)
    public final JsonCommandProvider getProvider() {
        return provider;
    }

    @Contract(pure = true)
    public final @Path String getPath() {
        return path;
    }
}
