package dev.denimred.littlethings.testmod;

import dev.denimred.littlethings.facets.Facet;
import dev.denimred.littlethings.facets.Facets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public final class PotionJugItem extends PotionItem {
    public static final int MAX_CHARGES = 8;
    public final Facet<Integer> charges = Facets.intFacet("Charges");

    public PotionJugItem() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_BREWING));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        var player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer p) CriteriaTriggers.CONSUME_ITEM.trigger(p, stack);

        if (!level.isClientSide) for (var effect : PotionUtils.getMobEffects(stack)) {
            if (effect.getEffect().isInstantenous()) {
                effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1.0);
            } else {
                entity.addEffect(new MobEffectInstance(effect));
            }
        }

        level.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                charges.set(stack, charges.getOr(stack, MAX_CHARGES) - 1);
            }
        }

        return (player != null && player.getAbilities().instabuild) || charges.getOr(stack, 0) > 0 ? stack : new ItemStack(FacetsTest.GLASS_JUG);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (!allowdedIn(category)) return;
        for(var potion : Registry.POTION) {
            if (potion != Potions.EMPTY) {
                items.add(PotionUtils.setPotion(new ItemStack(this), potion));
            }
        }
    }

    public float getVolume(ItemStack stack) {
        var charge = charges.get(stack);
        return charge != null ? (float) charge / MAX_CHARGES : 1f;
    }
}
