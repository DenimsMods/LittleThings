package dev.denimred.littlethings.commands.json.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.arguments.*;
import dev.denimred.littlethings.annotations.Resource;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.function.Supplier;

import static dev.denimred.littlethings.commands.json.util.ArgumentDeserializer.optionalParameter;
import static dev.denimred.littlethings.commands.json.util.ArgumentDeserializer.requireParameters;

/** Defines deserializers for all vanilla argument types. */
@SuppressWarnings("unused")
public final class VanillaArgumentDeserializers {
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";
    public static final String REGISTRY = "registry";

    private static final Map<ResourceLocation, ArgumentDeserializer.Named> ALL = new Object2ReferenceOpenHashMap<>();

    /** @see BoolArgumentType */
    public static final ArgumentDeserializer.Named BOOL = register("brigadier:bool", BoolArgumentType::bool);

    /** @see FloatArgumentType */
    public static final ArgumentDeserializer.Named FLOAT = register("brigadier:float", (typeId, parameters) -> {
        var min = optionalParameter(parameters, MIN, -Float.MAX_VALUE, JsonElement::getAsFloat);
        var max = optionalParameter(parameters, MAX, Float.MAX_VALUE, JsonElement::getAsFloat);
        return FloatArgumentType.floatArg(min, max);
    });

    /** @see DoubleArgumentType */
    public static final ArgumentDeserializer.Named DOUBLE = register("brigadier:double", (typeId, parameters) -> {
        var min = optionalParameter(parameters, MIN, -Double.MAX_VALUE, JsonElement::getAsDouble);
        var max = optionalParameter(parameters, MAX, Double.MAX_VALUE, JsonElement::getAsDouble);
        return DoubleArgumentType.doubleArg(min, max);
    });

    /** @see IntegerArgumentType */
    public static final ArgumentDeserializer.Named INTEGER = register("brigadier:integer", (typeId, parameters) -> {
        var min = optionalParameter(parameters, MIN, Integer.MIN_VALUE, JsonElement::getAsInt);
        var max = optionalParameter(parameters, MAX, Integer.MAX_VALUE, JsonElement::getAsInt);
        return IntegerArgumentType.integer(min, max);
    });

    /** @see LongArgumentType */
    public static final ArgumentDeserializer.Named LONG = register("brigadier:long", (typeId, parameters) -> {
        var min = optionalParameter(parameters, MIN, Long.MIN_VALUE, JsonElement::getAsLong);
        var max = optionalParameter(parameters, MAX, Long.MAX_VALUE, JsonElement::getAsLong);
        return LongArgumentType.longArg(min, max);
    });

    /** @see JsonParseException */
    public static final ArgumentDeserializer.Named STRING = register("brigadier:string", (typeId, parameters) -> {
        String stringType = optionalParameter(parameters, TYPE, "greedy", JsonElement::getAsString);
        return switch (stringType) {
            case "word" -> StringArgumentType.word();
            case "phrase" -> StringArgumentType.string();
            case "greedy" -> StringArgumentType.greedyString();
            default -> throw new JsonParseException("Unknown string type: " + stringType);
        };
    });

    /** @see EntityArgument */
    public static final ArgumentDeserializer.Named ENTITY = register("entity", (typeId, parameters) -> {
        requireParameters(typeId, parameters);
        String amount = parameters.get(AMOUNT).getAsString();
        boolean single = switch (amount) {
            case "single" -> true;
            case "multiple" -> false;
            default -> throw new JsonParseException("Unknown entity amount: " + amount);
        };
        String entityType = parameters.get(TYPE).getAsString();
        boolean playersOnly = switch (entityType) {
            case "players" -> true;
            case "entities" -> false;
            default -> throw new JsonParseException("Unknown entity type: " + entityType);
        };
        // lmao
        return single ? playersOnly ? EntityArgument.player() : EntityArgument.entity() : playersOnly ? EntityArgument.players() : EntityArgument.entities();
    });

    /** @see GameProfileArgument */
    public static final ArgumentDeserializer.Named GAME_PROFILE = register("game_profile", GameProfileArgument::gameProfile);

    /** @see BlockPosArgument */
    public static final ArgumentDeserializer.Named BLOCK_POS = register("block_pos", BlockPosArgument::blockPos);

    /** @see ColumnPosArgument */
    public static final ArgumentDeserializer.Named COLUMN_POS = register("column_pos", ColumnPosArgument::columnPos);

    /** @see Vec3Argument */
    public static final ArgumentDeserializer.Named VEC_3 = register("vec3", Vec3Argument::vec3);

    /** @see Vec2Argument */
    public static final ArgumentDeserializer.Named VEC_2 = register("vec2", Vec2Argument::vec2);

    /** @see BlockStateArgument */
    public static final ArgumentDeserializer.Named BLOCK_STATE = register("block_state", BlockStateArgument::block);

    /** @see BlockPredicateArgument */
    public static final ArgumentDeserializer.Named BLOCK_PREDICATE = register("block_predicate", BlockPredicateArgument::blockPredicate);

    /** @see ItemArgument */
    public static final ArgumentDeserializer.Named ITEM_STACK = register("item_stack", ItemArgument::item);

    /** @see ItemPredicateArgument */
    public static final ArgumentDeserializer.Named ITEM_PREDICATE = register("item_predicate", ItemPredicateArgument::itemPredicate);

    /** @see ColorArgument */
    public static final ArgumentDeserializer.Named COLOR = register("color", ColorArgument::color);

    /** @see ComponentArgument */
    public static final ArgumentDeserializer.Named COMPONENT = register("component", ComponentArgument::textComponent);

    /** @see MessageArgument */
    public static final ArgumentDeserializer.Named MESSAGE = register("message", MessageArgument::message);

    /** @see CompoundTagArgument */
    public static final ArgumentDeserializer.Named NBT_COMPOUND_TAG = register("nbt_compound_tag", CompoundTagArgument::compoundTag);

    /** @see NbtTagArgument */
    public static final ArgumentDeserializer.Named NBT_TAG = register("nbt_tag", NbtTagArgument::nbtTag);

    /** @see NbtPathArgument */
    public static final ArgumentDeserializer.Named NBT_PATH = register("nbt_path", NbtPathArgument::nbtPath);

    /** @see ObjectiveArgument */
    public static final ArgumentDeserializer.Named OBJECTIVE = register("objective", ObjectiveArgument::objective);

    /** @see ObjectiveCriteriaArgument */
    public static final ArgumentDeserializer.Named OBJECTIVE_CRITERIA = register("objective_criteria", ObjectiveCriteriaArgument::criteria);

    /** @see OperationArgument */
    public static final ArgumentDeserializer.Named OPERATION = register("operation", OperationArgument::operation);

    /** @see ParticleArgument */
    public static final ArgumentDeserializer.Named PARTICLE = register("particle", ParticleArgument::particle);

    /** @see AngleArgument */
    public static final ArgumentDeserializer.Named ANGLE = register("angle", AngleArgument::angle);

    /** @see RotationArgument */
    public static final ArgumentDeserializer.Named ROTATION = register("rotation", RotationArgument::rotation);

    /** @see ScoreboardSlotArgument */
    public static final ArgumentDeserializer.Named SCOREBOARD_SLOT = register("scoreboard_slot", ScoreboardSlotArgument::displaySlot);

    /** @see ScoreHolderArgument */
    public static final ArgumentDeserializer.Named SCORE_HOLDER = register("score_holder", (typeId, parameters) -> {
        requireParameters(typeId, parameters);
        String amount = parameters.get(AMOUNT).getAsString();
        boolean multiple = switch (amount) {
            case "single" -> false;
            case "multiple" -> true;
            default -> throw new JsonParseException("Unknown score holder amount: " + amount);
        };
        return multiple ? ScoreHolderArgument.scoreHolders() : ScoreHolderArgument.scoreHolder();
    });

    /** @see SwizzleArgument */
    public static final ArgumentDeserializer.Named SWIZZLE = register("swizzle", SwizzleArgument::swizzle);

    /** @see TeamArgument */
    public static final ArgumentDeserializer.Named TEAM = register("team", TeamArgument::team);

    /** @see SlotArgument */
    public static final ArgumentDeserializer.Named ITEM_SLOT = register("item_slot", SlotArgument::slot);

    /** @see ResourceLocationArgument */
    public static final ArgumentDeserializer.Named RESOURCE_LOCATION = register("resource_location", ResourceLocationArgument::id);

    /** @see MobEffectArgument */
    public static final ArgumentDeserializer.Named MOB_EFFECT = register("mob_effect", MobEffectArgument::effect);

    /** @see FunctionArgument */
    public static final ArgumentDeserializer.Named FUNCTION = register("function", FunctionArgument::functions);

    /** @see EntityAnchorArgument */
    public static final ArgumentDeserializer.Named ENTITY_ANCHOR = register("entity_anchor", EntityAnchorArgument::anchor);

    /** @see RangeArgument */
    public static final ArgumentDeserializer.Named INT_RANGE = register("int_range", RangeArgument::intRange);

    /** @see RangeArgument */
    public static final ArgumentDeserializer.Named FLOAT_RANGE = register("float_range", RangeArgument::floatRange);

    /** @see ItemEnchantmentArgument */
    public static final ArgumentDeserializer.Named ITEM_ENCHANTMENT = register("item_enchantment", ItemEnchantmentArgument::enchantment);

    /** @see EntitySummonArgument */
    public static final ArgumentDeserializer.Named ENTITY_SUMMON = register("entity_summon", EntitySummonArgument::id);

    /** @see DimensionArgument */
    public static final ArgumentDeserializer.Named DIMENSION = register("dimension", DimensionArgument::dimension);

    /** @see TimeArgument */
    public static final ArgumentDeserializer.Named TIME = register("time", TimeArgument::time);

    /** @see UuidArgument */
    public static final ArgumentDeserializer.Named UUID = register("uuid", UuidArgument::uuid);

    /** @see ResourceKeyArgument */
    public static final ArgumentDeserializer.Named RESOURCE = register("resource", (typeId, parameters) -> {
        requireParameters(typeId, parameters);
        var key = ResourceKey.createRegistryKey(new ResourceLocation(parameters.get(REGISTRY).getAsString()));
        return ResourceKeyArgument.key(key);
    });

    /** @see ResourceOrTagLocationArgument */
    public static final ArgumentDeserializer.Named RESOURCE_OR_TAG = register("resource_or_tag", (typeId, parameters) -> {
        requireParameters(typeId, parameters);
        var key = ResourceKey.createRegistryKey(new ResourceLocation(parameters.get(REGISTRY).getAsString()));
        return ResourceOrTagLocationArgument.resourceOrTag(key);
    });

    private VanillaArgumentDeserializers() {}

    /**
     * Retrieves a vanilla argument type.
     * @param typeId the ID of the given argument type.
     * @return the vanilla argument type associated with the type ID.
     */
    @Contract(pure = true)
    public static ArgumentDeserializer.Named get(ResourceLocation typeId) {
        var deserializer = ALL.get(typeId);
        if (deserializer == null) throw new JsonParseException("Unknown argument type: " + typeId);
        return deserializer;
    }

    private static ArgumentDeserializer.Named register(@Resource String typeId, ArgumentDeserializer deserializer) {
        var named = new ArgumentDeserializer.Named(new ResourceLocation(typeId), deserializer);
        ALL.put(named.typeId(), named);
        return named;
    }

    private static ArgumentDeserializer.Named register(@Resource String typeId, Supplier<ArgumentType<?>> deserializer) {
        return register(typeId, (_typeId, _obj) -> deserializer.get());
    }
}
