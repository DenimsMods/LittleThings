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

    public JsonCommandProvider(DataGenerator generator, @Namespace String namespace) {
        this.generator = generator;
        this.namespace = namespace;
    }

    /** This method will be called when the provider is run in order to build and assemble all the commands. */
    protected abstract void generateCommands();

    /** Adds the provided command to be generated. Works on child nodes; will find the root itself. */
    public void add(CommandBuilder command) {
        var root = command;
        while (root.isChild()) root = root.pop();
        commands.add(root.assemble());
    }

    public void add(RedirectBuilder redirect) {
        add(redirect.pop());
    }

    /** Creates a new command builder with the provided name. */
    @Contract(value = "_ -> new", pure = true)
    public CommandBuilder command(@Path String name) {
        return new CommandBuilder(this, null, name);
    }

    /** Creates a new command builder with a redirect to the desired target. */
    @Contract(value = "_, _ -> new", pure = true)
    public CommandBuilder alias(@Path String name, @Path String target) {
        return new CommandBuilder(this, null, name)
                .redirect(target).pop();
    }

    @Override
    public void run(HashCache cache) throws IOException {
        commands.clear();
        generateCommands();
        var root = new JsonObject();
        for (var command : commands) command.write(getNamespace(), root);
        var path = generator.getOutputFolder().resolve("data/" + getNamespace() + "/commands.json");
        DataProvider.save(GSON, cache, root, path);
    }

    @Override
    public String getName() {
        return "JSON Commands";
    }

    @Contract(pure = true)
    public final @Namespace String getNamespace() {
        return namespace;
    }
}
