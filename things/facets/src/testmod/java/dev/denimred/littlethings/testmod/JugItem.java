package dev.denimred.littlethings.testmod;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;

public class JugItem extends BottleItem {
    public JugItem() {
        super(new Properties().tab(CreativeModeTab.TAB_BREWING));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);
        var hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);

        var pos = hit.getBlockPos();
        if (!level.mayInteract(player, pos)) return InteractionResultHolder.pass(stack);
        if (!level.getFluidState(pos).is(FluidTags.WATER)) return InteractionResultHolder.pass(stack);

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        var potion = turnBottleIntoItem(stack, player, FacetsTest.POTION_JUG.getDefaultInstance());
        return InteractionResultHolder.sidedSuccess(potion, level.isClientSide());
    }
}
