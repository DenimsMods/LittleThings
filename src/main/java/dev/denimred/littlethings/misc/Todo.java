package dev.denimred.littlethings.misc;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

/**
 * A specialized error that indicates incomplete code.
 */
public final class Todo extends Error {
    private Todo() {
        super();
    }

    private Todo(String msg) {
        super(msg);
    }

    /**
     * Immediately throws an exception, but done in a way that satisfies type requirements.
     * Therefore, you can assign a variable to this function or return the result of this function and still have code that will compile.
     *
     * @param <NOTHING> the "returned" type; technically discarded as this function never returns.
     * @return nothing at all, as this function always throws an exception.
     */
    @Contract(value = "-> fail", pure = true)
    public static <NOTHING> NOTHING TODO() throws Todo {
        throw new Todo().trimStackTrace();
    }

    /**
     * Immediately throws an exception, but done in a way that satisfies type requirements.
     * Therefore, you can assign a variable to this function or return the result of this function and still have code that will compile.
     *
     * @param msg       the message to be displayed that explains what is incomplete.
     * @param <NOTHING> the "returned" type; technically discarded as this function never returns.
     * @return nothing at all, as this function always throws an exception.
     */
    @Contract(value = "_ -> fail", pure = true)
    public static <NOTHING> NOTHING TODO(String msg) throws Todo {
        throw new Todo(msg).trimStackTrace();
    }

    private Todo trimStackTrace() {
        var stackTrace = getStackTrace();
        if (stackTrace.length > 0) setStackTrace(Arrays.copyOfRange(stackTrace, 1, stackTrace.length));
        return this;
    }

    @Override
    public String toString() {
        var msg = getLocalizedMessage();
        return msg != null ? "TODO: " + msg : "TODO";
    }
}
