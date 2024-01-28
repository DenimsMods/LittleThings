package dev.denimred.littlethings.testmod;

import dev.denimred.littlethings.facets.Facet;
import dev.denimred.littlethings.facets.Facets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

public final class TestItem extends Item {
    public final Facet<CompoundTag> yoinked = Facets.tagFacet(FacetsTest.ID, "yoinked");

    public TestItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var stack = context.getItemInHand();
        var entityData = yoinked.get(stack);

        if (entityData == null) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        return EntityType.by(entityData).map(type -> type.create(level)).map(entity -> {
            yoinked.remove(stack);
            entity.load(entityData);
            entity.setPos(context.getClickLocation());
            entity.level.addFreshEntity(entity);
            return InteractionResult.CONSUME;
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack weird, Player player, LivingEntity entity, InteractionHand usedHand) {
        if (entity.level.isClientSide) return InteractionResult.SUCCESS;

        var entityData = new CompoundTag();
        if (!entity.save(entityData)) return InteractionResult.FAIL;

        yoinked.set(player.getItemInHand(usedHand), entityData);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return yoinked.isIn(stack);
    }
}
