package dev.denimred.littlethings.testmod;

import com.google.gson.JsonParseException;
import dev.denimred.littlethings.facets.Facet;
import dev.denimred.littlethings.facets.Facets;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class MobYoinkerItem extends Item {
    private static final String CUSTOM_NAME = "CustomName";

    public final Facet<CompoundTag> yoinked = Facets.tagFacet(FacetsTest.ID, "yoinked");

    public MobYoinkerItem() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.UNCOMMON));
    }

    private static @Nullable Component getEntityName(CompoundTag data) {
        if (!data.contains(CUSTOM_NAME, Tag.TAG_STRING)) return null;
        try {
            return Component.Serializer.fromJson(data.getString(CUSTOM_NAME));
        } catch (JsonParseException ignored) {}
        return null;
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand usedHand) {
        if (yoinked.isIn(stack) || entity.isDeadOrDying()) return InteractionResult.FAIL;
        if (entity.level.isClientSide) return InteractionResult.SUCCESS;

        var entityData = new CompoundTag();
        if (!entity.save(entityData)) return InteractionResult.FAIL;

        yoinked.set(player.getItemInHand(usedHand), entityData);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        var entityData = yoinked.get(stack);
        if (entityData == null) return;
        Optional.ofNullable(getEntityName(entityData)).or(() -> EntityType.by(entityData).map(EntityType::getDescription)).map(c -> c.copy().withStyle(ChatFormatting.GRAY)).ifPresent(tooltip::add);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return yoinked.isIn(stack);
    }
}
