package dev.denimred.littlethings.testmod;

import dev.denimred.littlethings.annotations.Resource;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class FacetsTest implements ModInitializer {
    public static final String ID = "testmod";
    public static final MobYoinkerItem MOB_YOINKER = item("mob_yoinker", new MobYoinkerItem());
    public static final JugItem GLASS_JUG = item("glass_jug", new JugItem());
    public static final PotionJugItem POTION_JUG = item("potion_jug", new PotionJugItem());

    @Override
    public void onInitialize() {}

    public static ResourceLocation res(@Resource.Path String path) {
        return new ResourceLocation(ID, path);
    }

    private static <T extends Item> T item(@Resource.Path String name, T item) {
        return Registry.register(Registry.ITEM, res(name), item);
    }
}
