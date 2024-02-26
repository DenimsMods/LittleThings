package dev.denimred.littlethings.facets;

import dev.denimred.littlethings.annotations.NbtType;
import joptsimple.internal.Strings;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static net.minecraft.nbt.Tag.TAG_END;

/**
 * A simple abstraction around {@link ItemStack} NBT data.
 *
 * @param <T> the type that this facet handles.
 */
public final class Facet<T> {
    static final Logger LOGGER = LogManager.getLogger();
    @VisibleForTesting final String[] path;
    @VisibleForTesting final String name;
    private final byte type;
    private final Reader<T> reader;
    private final Writer<T> writer;

    /**
     * Constructs a new facet of the desired type with the given parameters.
     *
     * @param type the NBT tag type that this facet processes. Using TAG_END will prevent the facet from checking for a matching tag type.
     * @param reader the function invoked to read data from item data tags.
     * @param writer the function invoked to write data to item data tags.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem the remaining elements in the path; the last element will become the facet's name.
     */
    public Facet(@NbtType byte type, Reader<T> reader, Writer<T> writer, String pathFirst, String... pathRem) {
        this.type = type;
        this.reader = reader;
        this.writer = writer;
        var remLength = pathRem.length;
        if (remLength == 0) {
            this.path = new String[0];
            this.name = pathFirst;
        } else {
            this.path = new String[remLength];
            this.path[0] = pathFirst;
            System.arraycopy(pathRem, 0, this.path, 1, remLength - 1);
            this.name = pathRem[remLength - 1];
        }
    }

    private static @Nullable CompoundTag getParentTag(ItemStack stack, String[] path) {
        if (!stack.hasTag()) return null;
        var tag = stack.getTag();
        assert tag != null; // Sanity check; hasTag covers this
        for (String key : path) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) return null;
            tag = tag.getCompound(key);
        }
        return tag;
    }

    private static CompoundTag getOrCreateParentTag(CompoundTag root, String[] path) {
        var tag = root;
        for (String key : path) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) tag.put(key, new CompoundTag());
            tag = tag.getCompound(key);
        }
        return tag;
    }

    private static CompoundTag getOrCreateParentTag(ItemStack stack, String[] path) {
        return getOrCreateParentTag(stack.getOrCreateTag(), path);
    }

    private boolean checkContains(CompoundTag tag) {
        return type == TAG_END ? tag.contains(name) : tag.contains(name, type);
    }

    /**
     * Creates an NBT predicate from this facet, primarily for use in datagen.
     *
     * @param expectation the value that's expected in the predicate.
     *
     * @return a new {@link NbtPredicate} that matches this facet with the expected value.
     */
    @SuppressWarnings("unused") // TODO: Make test for Facet.createPredicate()
    @Contract(value = "_ -> new", pure = true)
    public NbtPredicate createPredicate(T expectation) {
        return createPredicate(expectation, null);
    }

    /**
     * Creates an NBT predicate from this facet, primarily for use in datagen.
     *
     * @param expectation the value that's expected in the predicate.
     * @param adjustment an adjustment to apply to the final expected NBT tag, in case some data should be ignored.
     * @param <P> the expected tag type. Performs an unchecked cast, so be sure you know what the type will be to avoid a CCE.
     *
     * @return a new {@link NbtPredicate} that matches this facet with the expected value.
     */
    @SuppressWarnings("unchecked")
    @Contract(value = "_, _ -> new", pure = true)
    public <P extends Tag> NbtPredicate createPredicate(T expectation, @Nullable Consumer<P> adjustment) {
        var root = new CompoundTag();
        var parent = getOrCreateParentTag(root, path);
        writer.write(parent, name, expectation);
        if (adjustment != null) {
            var tag = parent.get(name);
            if (tag != null) adjustment.accept((P) tag);
        }
        return new NbtPredicate(root);
    }

    /**
     * Checks to see if the provided stack contains data that is managed by this facet.
     *
     * @param stack the item stack containing the NBT data to check.
     *
     * @return true if the given stack contains NBT data pertaining to this facet.
     */
    @Contract(pure = true)
    public boolean isIn(ItemStack stack) {
        var tag = getParentTag(stack, path);
        return tag != null && checkContains(tag);
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     *
     * @return the facet data stored in the stack, or null if no applicable data was present.
     */
    @Contract(pure = true)
    public @Nullable T get(ItemStack stack) {
        var tag = getParentTag(stack, path);
        return tag != null && checkContains(tag) ? reader.read(tag, name) : null;
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     * @param fallback the value to return if no data was present.
     *
     * @return the facet data stored in the stack, or the provided fallback if no applicable data was present.
     */
    @Contract(pure = true)
    public T getOr(ItemStack stack, T fallback) {
        @Nullable T result = get(stack);
        return result != null ? result : fallback;
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     * @param fallback the supplier to call and return the result of if no data was present.
     *
     * @return the facet data stored in the stack, or the result of the provided fallback if no applicable data was present.
     */
    @Contract(pure = true)
    public T getOrGet(ItemStack stack, Supplier<T> fallback) {
        @Nullable T result = get(stack);
        return result != null ? result : fallback.get();
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     *
     * @return the facet data stored in the stack.
     *
     * @throws NoSuchElementException if no applicable data was present in the stack.
     */
    @Contract(pure = true)
    public T getOrThrow(ItemStack stack) throws NoSuchElementException {
        @Nullable T result = get(stack);
        if (result == null) throw new NoSuchElementException();
        return result;
    }

    /**
     * Writes data to the provided stack.
     *
     * @param stack the item stack to write the provided value to.
     * @param value the value to be written to the stack.
     */
    public void set(ItemStack stack, T value) {
        var tag = getOrCreateParentTag(stack, path);
        writer.write(tag, name, value);
        var valueTag = tag.get(name);
        if (valueTag == null) {
            remove(stack);
        } else if (type != TAG_END && valueTag.getId() != type) {
            remove(stack);
            var joinedName = Strings.join(path, ".") + ":" + name;
            LOGGER.warn("Facet {} tried to write data with wrong NBT type (expected type {}, got {})", joinedName, type, valueTag.getId());
        }
    }

    /**
     * Modifies the data stored in the stack's NBT data, if present.
     * Typically only used for immutable data structures like integers and strings.
     *
     * @param stack the item stack to write the provided value to.
     * @param modifier the modifier function that will be applied to the stored value.
     *
     * @return true if the modifier was applied, false if no stored data was present.
     *
     * @see #mutate
     */
    public boolean modify(ItemStack stack, UnaryOperator<T> modifier) {
        @Nullable T value = get(stack);
        if (value == null) return false;
        set(stack, modifier.apply(value));
        return true;
    }

    /**
     * Mutates the data stored in the stack's NBT data, if present.
     * Similar to the modify function, but assumes the data type is mutable.
     * <p>
     * Always runs {@link #set} after the mutator has been applied to ensure that the changes made to the data are persisted.
     *
     * @param stack the item stack to write the provided value to.
     * @param mutator the mutator function that will be applied to the stored value.
     *
     * @return true if the mutator was applied, false if no stored data was present.
     *
     * @see #modify
     */
    public boolean mutate(ItemStack stack, Consumer<T> mutator) {
        @Nullable T value = get(stack);
        if (value == null) return false;
        mutator.accept(value);
        set(stack, value);
        return true;
    }

    /**
     * Removes data from the provided stack.
     *
     * @param stack the stack to remove data from.
     */
    public void remove(ItemStack stack) {
        if (!stack.hasTag()) return;
        var root = stack.getTag();
        assert root != null; // Sanity check; hasTag covers this

        // Special case for empty path
        if (path.length == 0) {
            stack.removeTagKey(name);
            return;
        }

        // Collect all tags along the path
        var tags = new CompoundTag[path.length];
        for (int i = 0; i < tags.length; i++) {
            var key = path[i];
            var parent = i == 0 ? root : tags[i - 1];
            tags[i] = parent.getCompound(key);
        }

        // Remove the data itself
        int lastIndex = tags.length - 1;
        tags[lastIndex].remove(name);

        // Recursively remove empty tags, starting from the end
        for (int i = lastIndex; i >= 0; i--) {
            if (!tags[i].isEmpty()) return;
            var key = path[i];
            if (i == 0) {
                stack.removeTagKey(key);
            } else {
                tags[i - 1].remove(key);
            }
        }
    }

    /**
     * Reads NBT data from a {@link CompoundTag} and maps it to the appropriate type.
     *
     * @param <T> the type of data that is to be read from the tag.
     */
    @FunctionalInterface
    public interface Reader<T> {
        /**
         * Reads data from the provided NBT tag.
         *
         * @param tag the tag that contains the data to read.
         * @param name the name of the element within the tag that represents the data to read.
         *
         * @return the data that was stored in the tag, or null if the data couldn't be read or was invalid.
         */
        @Contract(pure = true)
        @OverrideOnly
        @Nullable T read(CompoundTag tag, String name);
    }

    /**
     * Writes NBT data to a {@link CompoundTag} after mapping it from the associated type.
     *
     * @param <T> the type of data that is to be written to the tag.
     */
    @FunctionalInterface
    public interface Writer<T> {
        /**
         * Writes data to the provided NBT tag.
         *
         * @param tag the tag to write data to.
         * @param name the name of the data tag to be inserted into the containing tag.
         * @param value the data to be written to the tag.
         */
        @OverrideOnly
        void write(CompoundTag tag, String name, T value);
    }
}
