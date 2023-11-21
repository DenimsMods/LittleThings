package dev.denimred.littlethings.facet;

import dev.denimred.littlethings.annotations.NotNullEverything;
import dev.denimred.littlethings.facet.Facet.Reader;
import dev.denimred.littlethings.facet.Facet.Writer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.AbstractList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@NotNullEverything
final class ListTagDelegate<T> extends AbstractList<T> {
    private final ListTag raw;
    private final BiFunction<ListTag, Integer, T> reader;
    private final Function<T, Tag> writer;

    static <T> Reader<List<T>> reader(byte listType, BiFunction<ListTag, Integer, T> reader, Function<T, Tag> writer) {
        return (tag, name) -> new ListTagDelegate<>(tag.getList(name, listType), reader, writer);
    }

    static <T> Writer<List<T>> writer(Function<T, Tag> writer) {
        return (tag, name, list) -> {
            if (list.isEmpty()) {
                tag.remove(name);
            } else {
                var raw1 = new ListTag();
                for (T t : list) raw1.add(writer.apply(t));
                tag.put(name, raw1);
            }
        };
    }

    private ListTagDelegate(ListTag raw, BiFunction<ListTag, Integer, T> reader, Function<T, Tag> writer) {
        this.raw = raw;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public T get(int index) {
        return reader.apply(raw, index);
    }

    @Override
    public T set(int index, T element) {
        T existing = get(index);
        raw.set(index, writer.apply(element));
        return existing;
    }

    @Override
    public void add(int index, T element) {
        raw.add(index, writer.apply(element));
    }

    @Override
    public T remove(int index) {
        T existing = get(index);
        raw.remove(index);
        return existing;
    }

    @Override
    public int size() {
        return raw.size();
    }
}
