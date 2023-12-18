package dev.denimred.littlethings.commands.json.datagen;

import com.google.gson.JsonObject;
import dev.denimred.littlethings.annotations.Resource;
import dev.denimred.littlethings.annotations.Resource.Namespace;
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

/** A simple builder abstraction over {@link JsonCommand} that can be used with {@link JsonCommandProvider} to build commands during datagen. */
public class JsonCommandBuilder {
    protected final @Namespace String namespace;
    protected final @Nullable JsonCommandBuilder parent;

    protected final @Path String name;
    protected final @Path String path;
    protected final List<JsonCommandBuilder> arguments = new ArrayList<>();
    protected @Nullable ResourceLocation type = null;
    protected @Nullable JsonObject parameters = null;
    protected @Nullable Integer level = null;
    protected @Nullable ResourceLocation executable = null;
    protected @Nullable JsonCommandRedirectBuilder redirect = null;

    /**
     * Constructs a new JSON command builder.
     *
     * @param namespace the namespace of the command being built.
     * @param parent the parent command of this command. Can be null to denote a root command.
     * @param name the name of this command.
     */
    public JsonCommandBuilder(@Namespace String namespace, @Subst("parent") @Nullable JsonCommandBuilder parent, @Path String name) {
        this.namespace = namespace;
        this.parent = parent;
        this.name = name;
        this.path = parent != null ? parent.path + "/" + name : name;
    }

    /**
     * Specifies the argument type of this command/argument.
     *
     * @param type the type ID to associate with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder type(ResourceLocation type) {
        this.type = type;
        return this;
    }

    /**
     * Specifies the argument type of this command/argument.
     *
     * @param type the type ID to associate with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder type(@Resource String type) {
        return type(new ResourceLocation(type));
    }

    /**
     * Specifies the argument type of this command/argument.
     *
     * @param deserializer the named deserializer to associate with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder type(ArgumentDeserializer.Named deserializer) {
        return type(deserializer.typeId());
    }

    /**
     * Specifies an argument parameter value.
     *
     * @param name the name of the parameter to define.
     * @param value the parameter's value.
     *
     * @return the containing command builder (this).
     */
    @Contract("_, _ -> this")
    public JsonCommandBuilder parameter(String name, boolean value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    /**
     * Specifies an argument parameter value.
     *
     * @param name the name of the parameter to define.
     * @param value the parameter's value.
     *
     * @return the containing command builder (this).
     */
    @Contract("_, _ -> this")
    public JsonCommandBuilder parameter(String name, Number value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    /**
     * Specifies an argument parameter value.
     *
     * @param name the name of the parameter to define.
     * @param value the parameter's value.
     *
     * @return the containing command builder (this).
     */
    @Contract("_, _ -> this")
    public JsonCommandBuilder parameter(String name, String value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    /**
     * Specifies an argument parameter value.
     *
     * @param name the name of the parameter to define.
     * @param value the parameter's value.
     *
     * @return the containing command builder (this).
     */
    @Contract("_, _ -> this")
    public JsonCommandBuilder parameter(String name, Character value) {
        if (parameters == null) parameters = new JsonObject();
        parameters.addProperty(name, value);
        return this;
    }

    /**
     * Specifies the permission level of this command.
     *
     * @param level the integer permission level to restrict usage with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder level(int level) {
        this.level = level;
        return this;
    }

    /**
     * Specifies the permission level of this command.
     *
     * @param level the name of the permission level to restrict usage with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder level(@LevelString String level) {
        return level(JsonCommand.levelValue(level));
    }

    /**
     * Specifies the executable of this command.
     *
     * @param executable the ID of the executable to associate with.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder executable(ResourceLocation executable) {
        this.executable = executable;
        return this;
    }

    /**
     * Specifies the executable of this command.
     *
     * @param executable the path half of the executable's ID. This command's namespace will be used for the other half.
     *
     * @return the containing command builder (this).
     */
    @Contract("_ -> this")
    public JsonCommandBuilder executable(@Path String executable) {
        return executable(new ResourceLocation(namespace, executable));
    }

    /**
     * Specifies the executable of this command.
     * Will use this command's namespace and path as the executable ID.
     *
     * @return the containing command builder (this).
     */
    @Contract("-> this")
    public JsonCommandBuilder executable() {
        return executable(path);
    }

    /**
     * Constructs a new command/argument child and adds it to this command's arguments.
     *
     * @param name the name of the child command/argument.
     *
     * @return the new builder of the child command/argument.
     */
    @Contract("_ -> new")
    public JsonCommandBuilder argument(@Path String name) {
        var arg = new JsonCommandBuilder(namespace, this, name);
        arguments.add(arg);
        return arg;
    }

    /**
     * Constructs a new redirect builder and assigns this command's redirect to it.
     *
     * @param target the target of the new redirect.
     *
     * @return the new redirect builder.
     */
    @Contract("_ -> new")
    public JsonCommandRedirectBuilder redirect(@Path String target) {
        return redirect = new JsonCommandRedirectBuilder(target, this);
    }

    /**
     * Constructs a new redirect builder and assigns this command's redirect to it.
     *
     * @param target the split path of the redirect's target.
     *
     * @return the new redirect builder.
     */
    @Contract("_ -> new")
    public JsonCommandRedirectBuilder redirect(@Subst("target/path") String... target) {
        return redirect(JsonCommand.path(target));
    }

    /**
     * Retrieves the parent of this command builder, if applicable.
     *
     * @return the parent of this builder, or throws an exception if this command is a root.
     */
    @Contract(pure = true)
    public JsonCommandBuilder pop() {
        return Objects.requireNonNull(parent, "Cannot get parent of root");
    }

    /**
     * Constructs a new {@link JsonCommand} using the data provided by this builder.
     *
     * @return the assembled JSON command.
     */
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
    public final @Namespace String getNamespace() {
        return namespace;
    }

    @Contract(pure = true)
    public final @Path String getPath() {
        return path;
    }
}
