package dev.denimred.littlethings.misc;

import dev.denimred.littlethings.annotations.NotNullEverything;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@NotNullEverything
class TodoTest {
    @Test
    void TODO() {
        Todo todo = assertThrowsExactly(Todo.class, () -> {
            String string = Todo.TODO();
            System.out.println(string);
        }, "Todo should be thrown");
        assertNull(todo.getLocalizedMessage(), "Todo should not have a message");
        assertTodoStacktrace(todo);
    }

    @Test
    void TODOMsg() {
        Todo todo = assertThrowsExactly(Todo.class, () -> {
            int integer = Todo.TODO("Need to implement something");
            System.out.println(integer * 2);
        }, "Todo should be thrown");
        assertNotNull(todo.getLocalizedMessage(), "Todo should have a message");
        assertTodoStacktrace(todo);
    }

    private void assertTodoStacktrace(Todo todo) {
        var stackTrace = todo.getStackTrace();
        assertTrue(stackTrace.length > 0, "Todo stacktrace shouldn't be empty");
        assertEquals(getClass().getName(), stackTrace[0].getClassName(), "Todo stacktrace first element should be method calling class");
    }
}