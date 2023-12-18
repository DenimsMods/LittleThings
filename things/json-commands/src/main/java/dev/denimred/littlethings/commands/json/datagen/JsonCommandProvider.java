package dev.denimred.littlethings.commands.json.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.denimred.littlethings.annotations.Resource.Namespace;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommand;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** Data generation provider that creates commands.json files. */
public abstract class JsonCommandProvider implements DataProvider {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final DataGenerator generator;
    protected final @Namespace String namespace;
    protected final Set<JsonCommand> commands = new HashSet<>();

    /**
     * Constructs a new JSON command provider under the desired namespace.
     *
     * @param generator the generator this provider will work with.
     * @param namespace the namespace under which this provider will build its commands.
     */
    public JsonCommandProvider(DataGenerator generator, @Namespace String namespace) {
        this.generator = generator;
        this.namespace = namespace;
    }

    /** This method will be called when the provider is run in order to build and assemble all the commands. */
    protected abstract void generateCommands();

    /**
     * Adds the provided command to be generated. Works on child nodes; will find the root itself.
     *
     * @param command the builder that contains the full description of a command to be assembled.
     */
    public void add(JsonCommandBuilder command) {
        var root = command;
        while (root.isChild()) root = root.pop();
        commands.add(root.assemble());
    }

    /**
     * Overload for {@link #add(JsonCommandBuilder)} that accepts a redirect builder instead.
     * Will simply call {@link JsonCommandRedirectBuilder#pop()} to retrieve its command builder.
     *
     * @param redirect the redirect builder attached to its associated command builder.
     */
    public void add(JsonCommandRedirectBuilder redirect) {
        add(redirect.pop());
    }

    /**
     * Creates a new {@link JsonCommandBuilder}.
     *
     * @param name the name of the command being created.
     *
     * @return a fresh and empty command builder.
     */
    @Contract(value = "_ -> new", pure = true)
    public JsonCommandBuilder command(@Path String name) {
        return new JsonCommandBuilder(namespace, null, name);
    }

    /**
     * Overload for {@link JsonCommandBuilder} that initializes it with a redirect to a target command.
     * Used to create simple command aliases.
     *
     * @param name the name of the command being created.
     * @param target the name of the target to redirect to.
     *
     * @return a new command builder with its redirect already defined.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public JsonCommandBuilder alias(@Path String name, @Path String target) {
        return command(name).redirect(target).pop();
    }

    @Override
    public void run(HashCache cache) throws IOException {
        commands.clear();
        generateCommands();
        var root = new JsonObject();
        for (var command : commands) command.write(namespace, root);
        var path = generator.getOutputFolder().resolve("data/" + namespace + "/commands.json");
        DataProvider.save(GSON, cache, root, path);
    }

    @Override
    public String getName() {
        return "JSON Commands";
    }
}
