package dev.denimred.littlethings.assertions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.IllegalFormatException;

final class Util {
    private static final int TRIM_DEPTH = 1;

    private Util() {}

    @Contract("_ -> param1")
    static <E extends Throwable> @NotNull E trimStackTrace(@NotNull E e) {
        var stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            var clampedDepth = Math.min(TRIM_DEPTH, stackTrace.length - 1);
            if (clampedDepth <= 0) return e;
            e.setStackTrace(Arrays.copyOfRange(stackTrace, clampedDepth, stackTrace.length));
        }
        return e;
    }

    static @NotNull String safeFormat(@NotNull String msg, Object @NotNull ... args) {
        if (args.length == 0) return msg;
        try {
            return msg.formatted(args);
        } catch (IllegalFormatException ignored) {
            return msg;
        }
    }
}
