package dev.denimred.littlethings.facets;

import dev.denimred.littlethings.annotations.NotNullEverything;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
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

    private static Facet<Integer> intFacet() {
        return Facets.intFacet("test", "facet");
    }

    private static ItemStack freshStack() {
        return new ItemStack(Items.STICK);
    }

    @Test
    void initNameOnly() {
        Facet<Integer> facet = Facets.intFacet("some_facet");
        assertEquals("some_facet", facet.name);
        assertArrayEquals(new String[0], facet.path);
    }

    @Test
    void initWithPath() {
        Facet<Integer> facet = Facets.intFacet("some", "path", "thing", "some_facet");
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
        facet.set(stack, 1);
        assertEquals(1, facet.get(stack));
    }

    @Test
    void getOr() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertEquals(1, facet.getOr(stack, 1));
        facet.set(stack, 1);
        assertEquals(1, facet.getOr(stack, 0));
    }

    @Test
    void getOrGet() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertEquals(1, facet.getOrGet(stack, () -> 1));
        facet.set(stack, 1);
        assertEquals(1, facet.getOrGet(stack, () -> 0));
    }

    @Test
    void getOrThrow() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertThrowsExactly(NoSuchElementException.class, () -> facet.getOrThrow(stack));
        facet.set(stack, 1);
        assertEquals(1, assertDoesNotThrow(() -> facet.getOrThrow(stack)));
    }

    @Test
    void set() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        facet.set(stack, 1);
        assertEquals(1, facet.get(stack));
        facet.set(stack, 2);
        assertEquals(2, facet.get(stack));
    }

    @Test
    void modify() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertFalse(facet.modify(stack, i -> i + 1));
        facet.set(stack, 0);
        assertTrue(facet.modify(stack, i -> i + 1));
        assertEquals(1, facet.get(stack));
    }

    @Test
    void mutate() {
        Facet<ItemStack> facet = Facets.stackFacet("stack_facet");
        ItemStack stack = freshStack();
        assertFalse(facet.mutate(stack, s -> s.shrink(s.getCount())));
        facet.set(stack, freshStack());
        assertTrue(facet.mutate(stack, s -> s.shrink(s.getCount())));
        var stored = facet.getOr(stack, ItemStack.EMPTY);
        assertTrue(stored.isEmpty());
    }

    @Test
    void remove() {
        Facet<Integer> facet = intFacet();
        ItemStack stack = freshStack();
        assertFalse(facet.isIn(stack));
        facet.remove(stack);
        assertFalse(facet.isIn(stack));
        facet.set(stack, 1);
        assertTrue(facet.isIn(stack));
        facet.remove(stack);
        assertFalse(facet.isIn(stack));
    }
}