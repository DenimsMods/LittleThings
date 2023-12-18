package dev.denimred.littlethings.testmod;

import dev.denimred.littlethings.commands.json.datagen.JsonCommandProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import static dev.denimred.littlethings.commands.json.util.LevelString.ADMINS;
import static dev.denimred.littlethings.commands.json.util.VanillaArgumentDeserializers.*;
import static dev.denimred.littlethings.testmod.JsonCommandsTest.*;

@SuppressWarnings("MissingJavadoc")
public final class JsonCommandsProviderTest implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.addProvider(new JsonCommandProvider(generator, generator.getModId()) {
            @Override
            protected void generateCommands() {
                add(command(JSON_TEST)
                        .argument(LITERAL_CHILD).executable().pop()
                        .argument(INTEGER_CHILD).executable().level(ADMINS).type(INTEGER).parameter(MIN, 0).parameter(MAX, 100));
                add(alias(ALIAS_TEST, JSON_TEST));
                add(command(TRIPLE_LITERAL)
                        .redirect(JSON_TEST, LITERAL_CHILD).modifier().forks());
            }
        });
    }
}
