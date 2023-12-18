package dev.denimred.littlethings.commands.json;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.denimred.littlethings.annotations.Resource.Namespace;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.util.LazyRedirect;
import dev.denimred.littlethings.commands.json.util.LevelInt;
import dev.denimred.littlethings.commands.json.util.LevelString;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Represents an entire command. Capable of being written to a JSON file, and can itself be created from raw JSON data.
 *
 * @param name the name of the command, used either as the literal entered during execution or the name of the argument that displays during autocomplete.
 * @param type the ID of the argument type. If null, the command will be interpreted as a literal.
 * @param parameters the raw JSON object representing the argument parameters, or null if not parameters are defined.
 * @param level the permission level of the command. If null, will be interpreted as having no permission restriction.
 * @param executable the ID of the executable this command runs, or null if this command doesn't execute anything (i.e. is an intermediate argument).
 * @param redirect the redirect information of this command.
 * @param path the path of the command. Mainly used to generate the executable path if a simple boolean is used in the JSON file. Not serialized.
 * @param arguments the child arguments of this command, if any.
 */
public record JsonCommand(@Path String name,
                          @Nullable ResourceLocation type,
                          @Nullable JsonObject parameters,
                          @Nullable Integer level,
                          @Nullable ResourceLocation executable,
                          @Nullable @Subst("target/path") JsonCommandRedirect redirect,
                          @Path String path,
                          JsonCommand[] arguments) {

    public static final String TYPE = "type";
    public static final String PARAMETERS = "parameters";
    public static final String LEVEL = "level";
    public static final String EXECUTABLE = "executable";
    public static final String REDIRECT = "redirect";
    public static final String ARGUMENTS = "arguments";

    /**
     * Overload for {@link #path(String...)} that accepts a collection.
     *
     * @param path the split path to join.
     *
     * @return the joined path.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @Path String path(@Subst("path") Collection<String> path) {
        @Subst("path") var array = path.toArray(new String[0]);
        return path(array);
    }

    /**
     * Joins an array of strings into a "/" delimited string.
     *
     * @param path the split path to join.
     *
     * @return the joined path.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @Path String path(String... path) {
        var sj = new StringJoiner("/");
        for (var s : path) sj.add(s);
        @Subst("path") var output = sj.toString();
        return output;
    }

    /**
     * Maps a command permission level's string name to its integer value.
     *
     * @param level the name of the command permission level.
     *
     * @return the integer value of the command level.
     *
     * @throws JsonParseException if the string level isn't a known permission level.
     */
    @Contract(pure = true)
    @LevelInt
    public static int levelValue(String level) {
        return switch (level) {
            case LevelString.ALL -> LevelInt.ALL;
            case LevelString.MODERATORS -> LevelInt.MODERATORS;
            case LevelString.GAMEMASTERS -> LevelInt.GAMEMASTERS;
            case LevelString.ADMINS -> LevelInt.ADMINS;
            case LevelString.OWNERS -> LevelInt.OWNERS;
            default -> throw new JsonParseException("Unknown permission level: " + level);
        };
    }

    /**
     * Maps a command permission level integer to its string name.
     *
     * @param level the string name of the command permission level.
     *
     * @return the integer value of the command level, or null if the level has no known name.
     */
    @Contract(pure = true)
    @LevelString
    public static @Nullable String levelName(int level) {
        return switch (level) {
            case LevelInt.ALL -> LevelString.ALL;
            case LevelInt.MODERATORS -> LevelString.MODERATORS;
            case LevelInt.GAMEMASTERS -> LevelString.GAMEMASTERS;
            case LevelInt.ADMINS -> LevelString.ADMINS;
            case LevelInt.OWNERS -> LevelString.OWNERS;
            default -> null;
        };
    }

    /**
     * Reads all commands contained within the root JSON object.
     *
     * @param namespace the namespace which all the commands are under.
     * @param root the root object containing all the commands.
     *
     * @return an immutable list of the commands read from the root object.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @Unmodifiable List<JsonCommand> readAll(@Namespace String namespace, JsonObject root) {
        var commands = ImmutableList.<JsonCommand>builder();
        for (var entry : root.entrySet()) {
            @Subst("name") var name = entry.getKey();
            commands.add(read(namespace, name, entry.getValue().getAsJsonObject()));
        }
        return commands.build();
    }

    /**
     * Reads a single command (and its children) from a given JSON object.
     *
     * @param namespace the namespace which the command and its children are under.
     * @param name the name of the command being read.
     * @param obj the JSON object that represents this command.
     *
     * @return a new command read from the given object.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static JsonCommand read(@Namespace String namespace, @Path String name, JsonObject obj) {
        return read(namespace, name, name, obj);
    }

    private static JsonCommand read(@Namespace String namespace, @Path String name, @Path String path, JsonObject obj) {
        var type = obj.has(TYPE) ? new ResourceLocation(obj.get(TYPE).getAsString()) : null;
        var parameters = obj.has(PARAMETERS) ? obj.get(PARAMETERS).getAsJsonObject() : null;
        var level = readLevel(obj);
        var executable = readExecutable(obj, namespace, path);
        var redirect = obj.has(REDIRECT) ? JsonCommandRedirect.read(namespace, path, obj.get(REDIRECT)) : null;
        var arguments = readArguments(obj, namespace, path);
        return new JsonCommand(name, type, parameters, level, executable, redirect, path, arguments);
    }

    private static @Nullable Integer readLevel(JsonObject obj) {
        if (!obj.has(LEVEL)) return null;
        var primitive = obj.getAsJsonPrimitive(LEVEL);
        if (primitive.isNumber()) return primitive.getAsInt();
        return levelValue(primitive.getAsString());
    }

    private static @Nullable ResourceLocation readExecutable(JsonObject obj, @Namespace String namespace, @Path String path) {
        if (!obj.has(EXECUTABLE)) return null;
        var primitive = obj.getAsJsonPrimitive(EXECUTABLE);
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? new ResourceLocation(namespace, path) : null;
        } else if (primitive.isString()) {
            var str = primitive.getAsString();
            return str.indexOf(':') == -1 ? new ResourceLocation(namespace, str) : new ResourceLocation(str);
        }
        return null;
    }

    private static JsonCommand[] readArguments(JsonObject obj, @Namespace String namespace, @Path String path) {
        if (!obj.has(ARGUMENTS)) return new JsonCommand[0];
        var argObj = obj.get(ARGUMENTS).getAsJsonObject();
        var size = argObj.size();
        var arguments = new JsonCommand[size];
        var argNames = List.copyOf(argObj.keySet());
        for (int i = 0; i < size; i++) {
            @Subst("child") var argName = argNames.get(i);
            arguments[i] = read(namespace, argName, path + "/" + argName, argObj.get(argName).getAsJsonObject());
        }
        return arguments;
    }

    /**
     * Writes this command and all of its children to the parent JSON object.
     *
     * @param namespace the namespace which the command and its children are under.
     * @param parent the object to write to.
     */
    public void write(@Namespace String namespace, JsonObject parent) {
        var obj = new JsonObject();

        if (type != null) obj.addProperty(TYPE, type.toString());

        if (parameters != null) obj.add(PARAMETERS, parameters);

        if (level != null) {
            var levelName = levelName(level);
            if (levelName != null) {
                obj.addProperty(LEVEL, levelName);
            } else {
                obj.addProperty(LEVEL, level);
            }
        }

        if (executable != null) {
            var namespaceMatches = executable.getNamespace().equals(namespace);
            var pathMatches = executable.getPath().equals(path);
            if (namespaceMatches && pathMatches) {
                obj.addProperty(EXECUTABLE, true);
            } else if (namespaceMatches) {
                obj.addProperty(EXECUTABLE, executable.getPath());
            } else {
                obj.addProperty(EXECUTABLE, executable.toString());
            }
        }

        if (redirect != null) obj.add(REDIRECT, redirect.write(namespace, path));

        if (arguments.length > 0) {
            var argsRoot = new JsonObject();
            for (var arg : arguments) arg.write(namespace, argsRoot);
            obj.add(ARGUMENTS, argsRoot);
        }

        parent.add(name, obj);
    }

    /**
     * Registers this command and its children to the given dispatcher.
     *
     * @param manager the command manager that handles the argument deserializers and executables needed by this command and its children.
     * @param dispatcher the command dispatcher the command should register to.
     */
    public void register(JsonCommandManager manager, CommandDispatcher<CommandSourceStack> dispatcher) {
        if (assemble(manager, dispatcher) instanceof LiteralCommandNode<CommandSourceStack> literal) {
            dispatcher.getRoot().addChild(literal);
        } else {
            throw new IllegalArgumentException("Cannot register a non-literal root command");
        }
    }

    private CommandNode<CommandSourceStack> assemble(JsonCommandManager manager, CommandDispatcher<CommandSourceStack> dispatcher) {
        var builder = type == null ? Commands.literal(name) : Commands.argument(name, manager.getArgumentDeserializer(type).apply(type, parameters));
        if (level != null) builder.requires(source -> source.hasPermission(level));
        if (executable != null) builder.executes(manager.getExecutable(executable));
        for (var arg : arguments) builder.then(arg.assemble(manager, dispatcher));
        if (redirect == null) return builder.build();
        var modifier = redirect.modifier() != null ? manager.getRedirectModifier(redirect.modifier()) : null;
        var forks = redirect.forks() != null && redirect.forks();
        builder.forward(null, modifier, forks);
        return LazyRedirect.wrap(builder.build(), dispatcher, redirect.target());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonCommand that = (JsonCommand) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(parameters, that.parameters) && Objects.equals(level, that.level) && Objects.equals(executable, that.executable) && Objects.equals(redirect, that.redirect) && Objects.equals(path, that.path) && Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, type, parameters, level, executable, redirect, path);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    @Override
    public String toString() {
        return "JsonCommand{name='%s', type=%s, parameters=%s, level=%d, executable=%s, redirect=%s, path='%s', arguments=%s}".formatted(name, type, parameters, level, executable, redirect, path, Arrays.toString(arguments));
    }
}
