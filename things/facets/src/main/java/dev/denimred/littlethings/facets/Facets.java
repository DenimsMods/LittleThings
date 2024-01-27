package dev.denimred.littlethings.facets;

import com.mojang.serialization.Codec;
import dev.denimred.littlethings.annotations.NbtType;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static dev.denimred.littlethings.facets.Facet.LOGGER;
import static net.minecraft.nbt.Tag.*;

/**
 * Helper functions for working with {@linkplain Facet}s.
 */
@SuppressWarnings("unused")
public final class Facets {
    private Facets() {
        throw new AssertionError();
    }

    /**
     * Constructs a new boolean facet backed by the standard {@link CompoundTag} functions.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
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
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
     * @return a new UUID facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<UUID> uuidFacet(String pathFirst, String... pathRem) {
        return new Facet<>(TAG_INT_ARRAY, CompoundTag::getUUID, CompoundTag::putUUID, pathFirst, pathRem);
    }

    /**
     * Constructs a new item stack facet that removes data when inputting an empty stack.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
     * @return a new item stack facet.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<ItemStack> stackFacet(String pathFirst, String... pathRem) {
        return objectFacet(ItemStack::of, (stack, tag) -> {
            if (!stack.isEmpty()) stack.save(tag);
        }, pathFirst, pathRem);
    }

    /**
     * Constructs a new object facet of a desired type, wrapped by {@link CompoundTag} serialization.
     *
     * @param reader the function applied to the internal list tag to read the correct data type.
     * @param writer the function applied to the data type to convert it to a tag.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
     * @return a new object facet of the specified type, wrapped by a compound tag.
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static <T> Facet<T> objectFacet(Function<CompoundTag, T> reader, BiConsumer<T, CompoundTag> writer, String pathFirst, String... pathRem) {
        return new Facet<>(TAG_COMPOUND, (tag, name) -> reader.apply(tag.getCompound(name)), (tag, name, value) -> {
            var raw = new CompoundTag();
            writer.accept(value, raw);
            if (raw.isEmpty()) {
                tag.remove(name);
            } else {
                tag.put(name, raw);
            }
        }, pathFirst, pathRem);
    }

    /**
     * Constructs a new string list facet.
     *
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     *
     * @return a new string list facet of the given NBT type.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Facet<List<String>> stringListFacet(String pathFirst, String... pathRem) {
        return listFacet(TAG_STRING, ListTag::getString, StringTag::valueOf, pathFirst, pathRem);
    }

    /**
     * Constructs a new list facet of a particular type.
     *
     * @param listType the type of the list tag to be used.
     * @param reader the function applied to the internal list tag to read the correct data type.
     * @param writer the function applied to the data type to convert it to a tag.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     * @param <T> the data type that the internal list tag wraps.
     *
     * @return a new list facet of the given NBT type.
     */
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public static <T> Facet<List<T>> listFacet(@NbtType byte listType, BiFunction<ListTag, Integer, T> reader, Function<T, @Nullable Tag> writer, String pathFirst, String... pathRem) {
        return new Facet<>(TAG_LIST, ListTagDelegate.reader(listType, reader, writer), ListTagDelegate.writer(writer), pathFirst, pathRem);
    }

    /**
     * Constructs a new object list facet of a desired type, wrapped by {@link CompoundTag} serialization.
     *
     * @param reader the function that turns a compound tag into the desired type.
     * @param writer the function that turns the desired type into a compound tag.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     * @param <T> the data type that the facet processes.
     *
     * @return a new object facet of the specified type, wrapped by a compound tag.
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static <T> Facet<List<T>> objectListFacet(Function<CompoundTag, T> reader, BiConsumer<T, CompoundTag> writer, String pathFirst, String... pathRem) {
        return listFacet(TAG_COMPOUND, (list, i) -> reader.apply(list.getCompound(i)), value -> {
            var raw = new CompoundTag();
            writer.accept(value, raw);
            return raw.isEmpty() ? null : raw;
        }, pathFirst, pathRem);
    }

    /**
     * Constructs a new codec-backed facet of the specified type.
     *
     * @param codec the codec to read and write through.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     * @param <T> the data type that the facet processes.
     *
     * @return a new codec-backed facet of the specified type.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static <T> Facet<T> codecFacet(Codec<T> codec, String pathFirst, String... pathRem) {
        return new Facet<>(TAG_END, (tag, name) -> parseCodec(codec, tag, name), (tag, name, value) -> encodeCodec(codec, tag, name, value), pathFirst, pathRem);
    }

    private static <T> @Nullable T parseCodec(Codec<T> codec, CompoundTag tag, String name) {
        return codec.parse(NbtOps.INSTANCE, tag.get(name)).resultOrPartial(s -> LOGGER.warn("Failed to parse codec facet: {}", s)).orElse(null);
    }

    private static <T> void encodeCodec(Codec<T> codec, CompoundTag tag, String name, T value) {
        codec.encodeStart(NbtOps.INSTANCE, value).resultOrPartial(s -> LOGGER.warn("Failed to encode codec facet: {}", s)).ifPresent(t -> tag.put(name, t));
    }
}
