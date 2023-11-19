package dev.denimred.littlethings.facet;

import dev.denimred.littlethings.annotations.NbtType;
import dev.denimred.littlethings.annotations.NotNullEverything;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.NoSuchElementException;

/**
 * A simple abstraction around {@link ItemStack} NBT data.
 *
 * @param <T> the type that this facet handles.
 */
@NotNullEverything
public final class Facet<T> {
    @VisibleForTesting
    final String[] path;
    @VisibleForTesting
    final String name;
    private final byte type;
    private final Reader<T> reader;
    private final Writer<T> writer;

    /**
     * Constructs a new facet of the desired type with the given parameters.
     *
     * @param type      the NBT tag type that this facet processes.
     * @param reader    the function invoked to read data from item data tags.
     * @param writer    the function invoked to write data to item data tags.
     * @param pathFirst the first element in the path, exists to ensure at least one element is present in the path.
     * @param pathRem   the remaining elements in the path; the last element will become the facet's name.
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

    private static @Nullable CompoundTag getLastTag(ItemStack stack, String[] path) {
        if (!stack.hasTag()) return null;
        var tag = stack.getTag();
        assert tag != null; // Sanity check; hasTag covers this
        for (String key : path) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) return null;
            tag = tag.getCompound(key);
        }
        return tag;
    }

    private static CompoundTag getOrCreateLastTag(ItemStack stack, String[] path) {
        var tag = stack.getOrCreateTag();
        for (String key : path) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) tag.put(key, new CompoundTag());
            tag = tag.getCompound(key);
        }
        return tag;
    }

    /**
     * Checks to see if the provided stack contains data that is managed by this facet.
     *
     * @param stack the item stack containing the NBT data to check.
     * @return true if the given stack contains NBT data pertaining to this facet.
     */
    public boolean isIn(ItemStack stack) {
        var tag = getLastTag(stack, path);
        return tag != null && tag.contains(name, type);
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     * @return the facet data stored in the stack, or null if no applicable data was present.
     */
    public @Nullable T get(ItemStack stack) {
        var tag = getLastTag(stack, path);
        if (tag == null || !tag.contains(name, type)) return null;
        return reader.read(tag, name);
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack    the item stack containing the NBT data to retrieve.
     * @param fallback the value to return if no data was present.
     * @return the facet data stored in the stack, or the provided fallback if no applicable data was present.
     */
    public T getOr(ItemStack stack, T fallback) {
        @Nullable T result = get(stack);
        return result != null ? result : fallback;
    }

    /**
     * Retrieves data from the provided stack.
     *
     * @param stack the item stack containing the NBT data to retrieve.
     * @return the facet data stored in the stack.
     * @throws NoSuchElementException if no applicable data was present in the stack.
     */
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
     * @return the value previously held within the stack, or null if nothing was present.
     */
    public @Nullable T set(ItemStack stack, T value) {
        @Nullable T existing = get(stack);
        writer.write(getOrCreateLastTag(stack, path), name, value);
        return existing;
    }

    /**
     * Removes data from the provided stack.
     *
     * @param stack the stack to remove data from.
     * @return the value previously held within the stack, or null if nothing was present.
     */
    public @Nullable T remove(ItemStack stack) {
        @Nullable T existing = get(stack);
        if (!stack.hasTag()) return existing;
        var root = stack.getTag();
        assert root != null; // Sanity check; hasTag covers this

        // Special case for empty path
        if (path.length == 0 && root.contains(name, type)) {
            stack.removeTagKey(name);
            return existing;
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
        var last = tags[lastIndex];
        if (last.contains(name, type)) last.remove(name);

        // Recursively remove empty tags, starting from the end
        for (int i = lastIndex; i >= 0; i--) {
            if (!tags[i].isEmpty()) return existing;
            var key = path[i];
            if (i == 0) {
                stack.removeTagKey(key);
            } else {
                tags[i - 1].remove(key);
            }
        }

        return existing;
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
         * @param tag  the tag that contains the data to read.
         * @param name the name of the element within the tag that represents the data to read.
         * @return the data that was stored in the tag, mapped to the appropriate type.
         */
        @Contract(pure = true)
        T read(CompoundTag tag, String name);
    }

    /**
     * Writes NBT data to {@link CompoundTag} after mapping it from the associated type.
     *
     * @param <T> the type of data that is to be written to the tag.
     */
    @FunctionalInterface
    public interface Writer<T> {
        /**
         * Writes data to the provided NBT tag.
         *
         * @param tag   the tag to write data to.
         * @param name  the name of the data tag to be inserted into the containing tag.
         * @param value the data to be written to the tag.
         */
        @Contract(mutates = "param1")
        void write(CompoundTag tag, String name, T value);
    }
}
