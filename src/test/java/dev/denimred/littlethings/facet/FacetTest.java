package dev.denimred.littlethings.facet;

import dev.denimred.littlethings.annotations.NotNullEverything;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@NotNullEverything
class FacetTest {
    @BeforeAll
    static void boostrap() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    private static Facet<Integer> intFacet(String pathFirst, String... pathRem) {
        return new Facet<>(Tag.TAG_INT, CompoundTag::getInt, CompoundTag::putInt, pathFirst, pathRem);
    }

    private static Facet<Integer> intFacet() {
        return intFacet("test", "facet");
    }

    private static ItemStack freshStack() {
        return new ItemStack(Items.STICK);
    }

    @Test
    void initNameOnly() {
        Facet<Integer> facet = intFacet("some_facet");
        assertEquals("some_facet", facet.name);
        assertArrayEquals(new String[0], facet.path);
    }

    @Test
    void initWithPath() {
        Facet<Integer> facet = intFacet("some", "path", "thing", "some_facet");
        assertEquals("some_facet", facet.name);
        assertArrayEquals(new String[]{"some", "path", "thing"}, facet.path);
    }

    @Test
    void isIn() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertFalse(facet.isIn(stack));
        facet.set(stack, 1);
        assertTrue(facet.isIn(stack));
    }

    @Test
    void get() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertNull(facet.get(stack));
        assertNull(facet.set(stack, 1));
        assertEquals(1, facet.get(stack));
    }

    @Test
    void getOr() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertEquals(1, facet.getOr(stack, 1));
        assertNull(facet.set(stack, 1));
        assertEquals(1, facet.getOr(stack, 0));
    }

    @Test
    void getOrThrow() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertThrowsExactly(NoSuchElementException.class, () -> facet.getOrThrow(stack));
        assertNull(facet.set(stack, 1));
        assertEquals(1, assertDoesNotThrow(() -> facet.getOrThrow(stack)));
    }

    @Test
    void set() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertNull(facet.set(stack, 1));
        assertEquals(1, facet.get(stack));
        assertEquals(1, facet.set(stack, 2));
        assertEquals(2, facet.get(stack));
    }

    @Test
    void remove() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertFalse(facet.isIn(stack));
        assertNull(facet.remove(stack));
        assertFalse(facet.isIn(stack));
        assertNull(facet.set(stack, 1));
        assertTrue(facet.isIn(stack));
        assertEquals(1, facet.remove(stack));
        assertFalse(facet.isIn(stack));
    }
}