package dev.denimred.littlethings.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.alchemy.PotionUtils;

import static dev.denimred.littlethings.testmod.FacetsTest.res;

public final class FacetsTestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register((stack, layer) -> layer > 0 ? PotionUtils.getColor(stack) : -1, FacetsTest.POTION_JUG);
        ItemProperties.register(FacetsTest.POTION_JUG, res("potion_volume"), (stack, level, entity, i) -> FacetsTest.POTION_JUG.getVolume(stack));
        ItemProperties.register(FacetsTest.MOB_YOINKER, res("mob_yoinker_full"), (stack, level, entity, i) -> FacetsTest.MOB_YOINKER.yoinked.isIn(stack) ? 1f : 0f);
    }
}
