import org.intellij.lang.annotations.Language

@Language("java")
fun applyTemplates(assertions: List<PreparedAssertion>, functions: List<PreparedBiFunction>): String {
    val basicAsserts = MSG_KINDS.map { (params, contract, expression) ->
        ASSERT_TEMPLATE
            .replace(T_CONTRACT_ARGS, contract)
            .replace(T_MSG_PARAMS, params)
            .replace(T_MSG_EXPR, expression)
    }.reduce(::join)

    val eqGenericAsserts = EQ_MSG_KINDS.map { (params, contract, expression) ->
        ASSERT_EQUALS_GENERIC_TEMPLATE
            .replace(T_CONTRACT_ARGS, contract)
            .replace(T_MSG_PARAMS, params)
            .replace(T_MSG_EXPR, expression)
    }.reduce(::join).replace(T_EQ_MSG_TYPE, "BiFunction<A, B, @Nullable String>")

    val eqPrimAsserts = if (functions.isNotEmpty()) functions.map { (type, funcClass) ->
        EQ_MSG_KINDS.map { (params, contract, expression) ->
            ASSERT_EQUALS_PRIM_TEMPLATE
                .replace(T_EQ_PRIM_TYPE, type)
                .replace(T_CONTRACT_ARGS, contract)
                .replace(T_MSG_PARAMS, params)
                .replace(T_MSG_EXPR, expression)
        }.reduce(::join).replace(T_EQ_MSG_TYPE, "$funcClass<@Nullable String>")
    }.reduce(::join) else ""

    val notNullAsserts = MSG_KINDS.map { (params, contract, expression) ->
        ASSERT_NOT_NULL_TEMPLATE
            .replace(T_CONTRACT_ARGS, contract)
            .replace(T_MSG_PARAMS, params)
            .replace(T_MSG_EXPR, expression)
    }.reduce(::join)

    return assertions.map { (_, exceptionClass, prefix) ->
        basicAsserts
            .append(eqGenericAsserts)
            .append(eqPrimAsserts)
            .append(notNullAsserts)
            .replace(T_PREFIX, prefix)
            .replace(T_EXC_CLASS, exceptionClass)
    }.reduce(::join).trim()
}

private fun String.append(other: String) = this + other

private fun join(a: String, b: String) = "$a$b"

private fun prepareContractArgs(params: String): String {
    val count = params.split(",").size - 1
    if (count <= 0) return ""
    return ", _".repeat(count)
}

@Language("n/a") private const val T_PREFIX = "__prefix"
@Language("n/a") private const val T_EXC_CLASS = "__excClass"
@Language("n/a") private const val T_MSG_PARAMS = "__msgParams"
@Language("n/a") private const val T_MSG_EXPR = "__msgExpr"
@Language("n/a") private const val T_CONTRACT_ARGS = "__contractArgs"
@Language("n/a") private const val T_EQ_PRIM_TYPE = "__eqType"
@Language("n/a") private const val T_EQ_MSG_TYPE = "__eqMsgType"

data class PreparedMessageParams(val params: String, val contract: String, val expression: String) {
    constructor(params: String, expression: String) : this(params, prepareContractArgs(params), expression)
}

val NO_MSG = PreparedMessageParams("", "")
val SIMPLE_MSG = PreparedMessageParams(", @NotNull String message", "message")
val FORMATTED_MSG = PreparedMessageParams(", @PrintFormat @NotNull String message, @Nullable Object @NotNull ... args", "safeFormat(message, args)")
val LAZY_MSG = PreparedMessageParams(", @NotNull Supplier<@Nullable String> lazyMessage", "lazyMessage.get()")
val EQ_FORMATTED_MSG = PreparedMessageParams(", @PrintFormat @NotNull String message", "safeFormat(message, a, b)")
val EQ_LAZY_MSG = PreparedMessageParams(", @NotNull $T_EQ_MSG_TYPE lazyMessage", "lazyMessage.apply(a, b)")

private val MSG_KINDS = listOf(NO_MSG, SIMPLE_MSG, FORMATTED_MSG, LAZY_MSG)
private val EQ_MSG_KINDS = listOf(NO_MSG, EQ_FORMATTED_MSG, EQ_LAZY_MSG)

@Language("java")
private const val ASSERT_TEMPLATE = """
                @Contract(value = "false$T_CONTRACT_ARGS -> fail", pure = true)
                public static void $T_PREFIX(boolean condition$T_MSG_PARAMS) {
                    if (!condition) throw trimStackTrace(new $T_EXC_CLASS($T_MSG_EXPR));
                }
"""

@Language("java")
private const val ASSERT_NOT_NULL_TEMPLATE = """
                @Contract(value = "null$T_CONTRACT_ARGS -> fail; !null$T_CONTRACT_ARGS -> param1", pure = true)
                public static <T> @NotNull T ${T_PREFIX}NotNull(@Nullable T obj$T_MSG_PARAMS) {
                    if (obj == null) throw trimStackTrace(new $T_EXC_CLASS($T_MSG_EXPR));
                    return obj;
                }
"""

@Language("java")
private const val ASSERT_EQUALS_GENERIC_TEMPLATE = """
                @Contract(value = "null, !null$T_CONTRACT_ARGS -> fail; !null, null$T_CONTRACT_ARGS -> fail", pure = true)
                public static <A, B> void ${T_PREFIX}Equals(@Nullable A a, @Nullable B b$T_MSG_PARAMS) {
                    if (Objects.equals(a, b)) throw trimStackTrace(new $T_EXC_CLASS($T_MSG_EXPR));
                }
"""

@Language("java")
private const val ASSERT_EQUALS_PRIM_TEMPLATE = """
                @Contract(pure = true)
                public static void ${T_PREFIX}Equals($T_EQ_PRIM_TYPE a, $T_EQ_PRIM_TYPE b$T_MSG_PARAMS) {
                    if (a != b) throw trimStackTrace(new $T_EXC_CLASS($T_MSG_EXPR));
                }
"""