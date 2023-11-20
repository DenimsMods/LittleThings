package dev.denimred.littlethings.facet;

import dev.denimred.littlethings.annotations.NbtType;
import dev.denimred.littlethings.annotations.NotNullEverything;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

import static dev.denimred.littlethings.facet.Facet.Writer.remove;
import static net.minecraft.nbt.Tag.*;

/**
 * Helper functions for working with {@linkplain Facet}s.
 */
@SuppressWarnings("unused")
@NotNullEverything
public final class Facets {
    /**
     * Constructs a new boolean facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new boolean facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Boolean> booleanFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_BYTE, CompoundTag::getBoolean, CompoundTag::putBoolean, pathFirst, pathRem);
    }

    /**
     * Constructs a new byte facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new byte facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Byte> byteFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_BYTE, CompoundTag::getByte, CompoundTag::putByte, pathFirst, pathRem);
    }

    /**
     * Constructs a new short facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new short facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Short> shortFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_SHORT, CompoundTag::getShort, CompoundTag::putShort, pathFirst, pathRem);
    }

    /**
     * Constructs a new integer facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new integer facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Integer> intFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_INT, CompoundTag::getInt, CompoundTag::putInt, pathFirst, pathRem);
    }

    /**
     * Constructs a new long facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new long facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Long> longFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_LONG, CompoundTag::getLong, CompoundTag::putLong, pathFirst, pathRem);
    }

    /**
     * Constructs a new float facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new float facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Float> floatFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_FLOAT, CompoundTag::getFloat, CompoundTag::putFloat, pathFirst, pathRem);
    }

    /**
     * Constructs a new double facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new double facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<Double> doubleFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_DOUBLE, CompoundTag::getDouble, CompoundTag::putDouble, pathFirst, pathRem);
    }

    /**
     * Constructs a new byte array facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new byte array facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<byte[]> byteArrayFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_BYTE_ARRAY, CompoundTag::getByteArray, CompoundTag::putByteArray, pathFirst, pathRem);
    }

    /**
     * Constructs a new integer array facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new integer array facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<int[]> intArrayFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_INT_ARRAY, CompoundTag::getIntArray, CompoundTag::putIntArray, pathFirst, pathRem);
    }

    /**
     * Constructs a new long array facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new long array facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<long[]> longArrayFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_LONG_ARRAY, CompoundTag::getLongArray, CompoundTag::putLongArray, pathFirst, pathRem);
    }

    /**
     * Constructs a new string facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new string facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<String> stringFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_STRING, CompoundTag::getString, CompoundTag::putString, pathFirst, pathRem);
    }

    /**
     * Constructs a new compound tag facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new compound tag facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<CompoundTag> tagFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_COMPOUND, CompoundTag::getCompound, CompoundTag::put, pathFirst, pathRem);
    }

    /**
     * Constructs a new UUID facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new UUID facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<UUID> uuidFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_INT_ARRAY, CompoundTag::getUUID, CompoundTag::putUUID, pathFirst, pathRem);
    }

    /**
     * Constructs a new list tag facet of a particular type backed by the standard {@link CompoundTag} functions.
     *
     * @param listType  the type of the list tag to be used.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new list tag facet of the given NBT type.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Facet<ListTag> listFacet(@NbtType byte listType, String pathFirst, String... pathRem) {
        return new Facet<>(TAG_LIST, (tag, name) -> tag.getList(name, listType), CompoundTag::put, pathFirst, pathRem);
    }

    /**
     * Constructs a new item stack facet that removes data when inputting an empty stack.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
     * @return a new item stack facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<ItemStack> stackFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_COMPOUND, (tag, name) -> ItemStack.of(tag.getCompound(name)), (tag, name, value) -> {
            if (value.isEmpty()) remove();
            tag.put(name, value.save(new CompoundTag()));
        }, pathFirst, pathRem);
    }

    private Facets() {
        throw new AssertionError();
    }
}
