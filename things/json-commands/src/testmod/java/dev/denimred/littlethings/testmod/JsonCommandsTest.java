package dev.denimred.littlethings.testmod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.denimred.littlethings.annotations.Resource.Namespace;
import dev.denimred.littlethings.annotations.Resource.Path;
import dev.denimred.littlethings.commands.json.JsonCommandManager;
import dev.denimred.littlethings.commands.json.util.VanillaArgumentDeserializers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.List;

import static dev.denimred.littlethings.commands.json.JsonCommand.path;

@SuppressWarnings("MissingJavadoc")
public final class JsonCommandsTest implements ModInitializer {
    public static final @Namespace String ID = "testmod";

    public static final String JSON_TEST = "json_test";
    public static final String LITERAL_CHILD = "literal_child";
    public static final String INTEGER_CHILD = "integer_child";
    public static final String ALIAS_TEST = "alias_test";
    public static final String TRIPLE_LITERAL = "triple_literal";

    private static final JsonCommandManager COMMANDS = new JsonCommandManager(ID)
            .setExecutable(path(JSON_TEST, LITERAL_CHILD), ctx -> {
                var source = ctx.getSource();
                source.sendSuccess(new TextComponent("Good job!"), false);
            })
            .setExecutable(path(JSON_TEST, INTEGER_CHILD), ctx -> {
                var i = IntegerArgumentType.getInteger(ctx, INTEGER_CHILD);
                var source = ctx.getSource();
                source.sendSuccess(new TextComponent("Good job x%s!".formatted(i)), false);
                return i;
            })
            .setArgumentDeserializer(VanillaArgumentDeserializers.INTEGER)
            .setRedirectModifier(TRIPLE_LITERAL, ctx -> {
                var source = ctx.getSource();
                return List.of(source, source, source);
            });

    public static ResourceLocation res(@Path String path) {
        return new ResourceLocation(ID, path);
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> COMMANDS.setDispatcher(dispatcher));
        var listener = new DelegatedResourceReloadListener(res("json_commands"), COMMANDS);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listener);
    }
}
