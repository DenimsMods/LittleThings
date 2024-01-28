package dev.denimred.littlethings.testmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class FacetsTest implements ModInitializer {
    public static final String ID = "testmod";

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new ResourceLocation(ID, "test_item"), new TestItem());
    }
}
