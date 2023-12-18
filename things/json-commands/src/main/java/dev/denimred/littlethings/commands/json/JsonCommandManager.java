package dev.denimred.littlethings.commands.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.logging.LogUtils;
import dev.denimred.littlethings.annotations.Resource.Namespace;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.util.ArgumentDeserializer;
import dev.denimred.littlethings.commands.json.util.UnitCommand;
import dev.denimred.littlethings.commands.json.util.VanillaArgumentDeserializers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the reading and registering of {@link JsonCommand} instances.
 * <p>
 * For most users, this will be the only object they need to interact with.
 * To make use of it, an instance must first be created, then it must be hooked into the command registration system
 * as well as the datapack-side resource reload system.
 * The method of hooking this manager into the aforementioned system differs depending on the mod loader.
 * If the mod loader being used does not offer these hooks, you may need to create them manually.
 * <p>
 * Keep in mind that the dispatcher will need to be set via {@link #setDispatcher} every time data reloads since the dispatcher changes each time.
 * This is also due to the fact that obtaining the dispatcher from the reload listener game executor is unreliable at best.
 * Sometimes the game executor is a dedicated server, sometimes it's a client, and other times it's a {@link Runnable#run()} method reference.
 * As such, requiring that the dispatcher be set manually is the only real way to ensure the commands can always be registered.
 * <p>
 * Lastly, be sure to invoke {@link #setExecutable}, {@link #setArgumentDeserializer}, and {@link #setRedirectModifier} if needed.
 * These methods handle the code-side functionality of JSON commands, as such things cannot be reasonably described in JSON.
 */
@SuppressWarnings("unused")
public class JsonCommandManager extends SimplePreparableReloadListener<@Unmodifiable List<JsonCommand>> {
    protected static final String FILENAME = "commands.json";
    protected static final Logger LOGGER = LogUtils.getLogger();

    protected final @Namespace String namespace;
    protected final Gson gson;

    protected final Map<ResourceLocation, Command<CommandSourceStack>> executables = new HashMap<>();
    protected final Map<ResourceLocation, RedirectModifier<CommandSourceStack>> redirectModifiers = new HashMap<>();
    protected final Map<ResourceLocation, ArgumentDeserializer> argumentDeserializers = new HashMap<>();
    protected @Nullable CommandDispatcher<CommandSourceStack> dispatcher = null;

    /**
     * Constructs a new JSON command manager. Only one needs to be created per-namespace.
     *
     * @param namespace the namespace under which the manager will read its commands.
     *
     * @see #JsonCommandManager(String, Gson)
     */
    public JsonCommandManager(@Namespace String namespace) {
        this(namespace, new Gson());
    }

    /**
     * Constructs a new JSON command manager. Only one needs to be created per-namespace.
     *
     * @param namespace the namespace under which the manager will read its commands.
     * @param gson the GSON instance in charge of reading the JSON data that stores the commands.
     *
     * @see #JsonCommandManager(String)
     */
    public JsonCommandManager(@Namespace String namespace, Gson gson) {
        this.namespace = namespace;
        this.gson = gson;
    }

    /**
     * Sets the dispatcher to be used next time commands are registered.
     * This must be invoked every time resources reload since a new dispatcher is usually created each time.
     *
     * @param dispatcher the dispatcher to register commands to.
     */
    public void setDispatcher(CommandDispatcher<CommandSourceStack> dispatcher) {
        this.dispatcher = dispatcher;
        LOGGER.debug("Dispatcher set for {}", namespace);
    }

    /**
     * Sets the executable code to be run by commands that reference its path.
     *
     * @param id the complete ID of the executable.
     * @param executable the code to be run when the command is executed.
     *
     * @return the containing manager (this).
     *
     * @see #setExecutable(String, Command)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setExecutable(ResourceLocation id, Command<CommandSourceStack> executable) {
        executables.put(id, executable);
        LOGGER.debug("Executable '{}' set for {}", id, namespace);
        return this;
    }

    /**
     * Sets the executable code to be run by commands that reference its path.
     *
     * @param path the path half of the executable; the manager's namespace will be used for the other half.
     * @param executable the code to be run when the command is executed.
     *
     * @return the containing manager (this).
     *
     * @see #setExecutable(ResourceLocation, Command)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setExecutable(@Path String path, Command<CommandSourceStack> executable) {
        return setExecutable(new ResourceLocation(namespace, path), executable);
    }

    /**
     * Sets the executable code to be run by commands that reference its path.
     *
     * @param id the complete ID of the executable.
     * @param executable the code to be run when the command is executed.
     *
     * @return the containing manager (this).
     *
     * @see #setExecutable(String, UnitCommand)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setExecutable(ResourceLocation id, UnitCommand<CommandSourceStack> executable) {
        return setExecutable(id, (Command<CommandSourceStack>) executable);
    }

    /**
     * Sets the executable code to be run by commands that reference its path.
     *
     * @param path the path half of the executable; the manager's namespace will be used for the other half.
     * @param executable the code to be run when the command is executed.
     *
     * @return the containing manager (this).
     *
     * @see #setExecutable(ResourceLocation, UnitCommand)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setExecutable(@Path String path, UnitCommand<CommandSourceStack> executable) {
        return setExecutable(path, (Command<CommandSourceStack>) executable);
    }

    /**
     * Sets the redirect modifier code to be run upon redirection of commands that reference its path.
     *
     * @param id the complete ID of the redirect modifier.
     * @param modifier the code to be run when the command is redirected.
     *
     * @return the containing manager (this).
     *
     * @see #setRedirectModifier(String, RedirectModifier)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setRedirectModifier(ResourceLocation id, RedirectModifier<CommandSourceStack> modifier) {
        redirectModifiers.put(id, modifier);
        LOGGER.debug("Redirect modifier '{}' set for {}", id, namespace);
        return this;
    }

    /**
     * Sets the redirect modifier code to be run upon redirection of commands that reference its path.
     *
     * @param path the path half of the executable; the manager's namespace will be used for the other half.
     * @param modifier the code to be run when the command is redirected.
     *
     * @return the containing manager (this).
     *
     * @see #setRedirectModifier(ResourceLocation, RedirectModifier)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setRedirectModifier(@Path String path, RedirectModifier<CommandSourceStack> modifier) {
        return setRedirectModifier(new ResourceLocation(namespace, path), modifier);
    }

    /**
     * Sets the deserializer for argument types that reference its type ID.
     *
     * @param typeId the ID of the argument type the deserializer works with.
     * @param deserializer the deserializer that will read JSON data to produce an argument type instance.
     *
     * @return the containing manager (this).
     *
     * @see #setArgumentDeserializer(ArgumentDeserializer.Named)
     */
    @Contract("_, _ -> this")
    public JsonCommandManager setArgumentDeserializer(ResourceLocation typeId, ArgumentDeserializer deserializer) {
        argumentDeserializers.put(typeId, deserializer);
        LOGGER.debug("Argument type '{}' deserializer set for {}", typeId, namespace);
        return this;
    }

    /**
     * Sets the deserializer for argument types that reference the deserializer's type ID.
     *
     * @param deserializer the named deserializer that will read JSON data to produce an argument type instance.
     *
     * @return the containing manager (this).
     *
     * @see #setArgumentDeserializer(ResourceLocation, ArgumentDeserializer)
     */
    @Contract("_ -> this")
    public JsonCommandManager setArgumentDeserializer(ArgumentDeserializer.Named deserializer) {
        return setArgumentDeserializer(deserializer.typeId(), deserializer);
    }

    /**
     * Retrieves an executable stored via {@link #setExecutable}.
     *
     * @param id the complete ID of the executable command to retrieve.
     *
     * @return the executable command instance to be registered to an argument builder.
     */
    @Contract(pure = true)
    public Command<CommandSourceStack> getExecutable(ResourceLocation id) {
        return Objects.requireNonNull(executables.get(id), "Executable not found: " + id);
    }

    /**
     * Retrieves a redirect modifier stored via {@link #setRedirectModifier}.
     *
     * @param id the complete ID of the redirect modifier to retrieve.
     *
     * @return the redirect modifier instance to be registered to an argument builder.
     */
    @Contract(pure = true)
    public RedirectModifier<CommandSourceStack> getRedirectModifier(ResourceLocation id) {
        return Objects.requireNonNull(redirectModifiers.get(id), "Redirect modifier not found: " + id);
    }

    /**
     * Retrieves an argument deserializer stored via {@link #setArgumentDeserializer}.
     * Will fall back to {@link VanillaArgumentDeserializers} if the desired type isn't defined in this manager.
     *
     * @param typeId the ID of the argument type being used.
     *
     * @return the first argument deserializer that is capable of handling the desired type.
     */
    @Contract(pure = true)
    public ArgumentDeserializer getArgumentDeserializer(ResourceLocation typeId) {
        return Objects.requireNonNullElseGet(argumentDeserializers.get(typeId), () -> VanillaArgumentDeserializers.get(typeId));
    }

    @Override
    protected @Unmodifiable List<JsonCommand> prepare(ResourceManager manager, ProfilerFiller profiler) {
        try {
            profiler.startTick();
            var resource = manager.getResource(new ResourceLocation(namespace, FILENAME));
            return parse(resource, profiler);
        } catch (IOException e) {
            return List.of();
        } finally {
            profiler.endTick();
        }
    }

    @Override
    protected void apply(@Unmodifiable List<JsonCommand> commands, ResourceManager manager, ProfilerFiller profiler) {
        if (dispatcher == null) {
            LOGGER.warn("Failed to register commands for {}; command dispatcher has not been set", namespace);
            return;
        }

        try {
            profiler.startTick();
            for (var command : commands) {
                try {
                    profiler.push(command.name());
                    command.register(this, dispatcher);
                } catch (Exception e) {
                    LOGGER.warn("Failed to register command %s:%s".formatted(namespace, command.name()), e);
                } finally {
                    profiler.pop();
                }
            }
            LOGGER.info("Finished registering commands for {}", namespace);
        } finally {
            profiler.endTick();
            dispatcher = null;
        }
    }

    protected @Unmodifiable List<JsonCommand> parse(Resource resource, ProfilerFiller profiler) {
        try {
            profiler.push(resource.getSourceName());
            try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                profiler.push("parse");
                var root = GsonHelper.fromJson(gson, reader, JsonObject.class);
                return root != null ? JsonCommand.readAll(namespace, root) : List.of();
            } catch (RuntimeException | IOException e) {
                LOGGER.warn("Invalid %s in pack '%s' for %s".formatted(FILENAME, resource.getSourceName(), namespace), e);
                return List.of();
            } finally {
                profiler.pop();
            }
        } finally {
            profiler.pop();
        }
    }
}
