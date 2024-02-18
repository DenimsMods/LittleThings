
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import org.intellij.lang.annotations.Language
import java.io.OutputStream

private const val ASSERT = "assert-"
private val NO_DEPS = Dependencies(true)

private fun OutputStream.write(@Language("java") str: String) = use { it.write(str.toByteArray()) }

data class PreparedAssertion(val path: String, val className: String, val prefix: String)
data class PreparedBiFunction(val type: String, val className: String)

private class GenProcessor(private val args: Map<String, String>, private val gen: CodeGenerator): SymbolProcessor {
    private lateinit var pkg: String
    private lateinit var funcPkg : String
    private lateinit var clazz: String
    private lateinit var functions: List<PreparedBiFunction>
    private lateinit var assertions: List<PreparedAssertion>

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val output by args
        pkg = output.substringBeforeLast('.')
        funcPkg = "$pkg.func"
        clazz = output.substringAfterLast('.')

        functions = args["functions"]?.let { functions ->
            functions.split('|').map {
                PreparedBiFunction(it, "Bi${it.replaceFirstChar(Char::uppercaseChar)}Function")
            }
        } ?: emptyList()

        assertions = args.mapNotNull { (key, value) ->
            if (!key.startsWith(ASSERT)) return@mapNotNull null
            PreparedAssertion(value, value.substringAfterLast('.'), key.removePrefix(ASSERT))
        }

        return emptyList()
    }

    override fun finish() {
        for ((type, className) in functions) gen.createNewFile(NO_DEPS, funcPkg, className, "java").write("""
            package $funcPkg;
            
            import org.jetbrains.annotations.ApiStatus;
            
            @ApiStatus.NonExtendable
            @FunctionalInterface
            public interface $className<T> {
                T apply($type a, $type b);
            }
            
        """.trimIndent())

        gen.createNewFile(NO_DEPS, pkg, clazz, "java").write("""
            package $pkg;
            ${if (functions.isNotEmpty()) "\n            import $pkg.func.*;" else ""}
            import org.intellij.lang.annotations.PrintFormat;
            import org.jetbrains.annotations.Contract;
            import org.jetbrains.annotations.NotNull;
            import org.jetbrains.annotations.Nullable;
            
            import java.util.Objects;
            import java.util.function.BiFunction;
            import java.util.function.Supplier;

            import static dev.denimred.littlethings.assertions.Util.*;
            
            public final class $clazz {
                private $clazz() {}
                
                ${applyTemplates(assertions, functions)}
            }
            
        """.trimIndent())
    }
}

class GenProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return GenProcessor(environment.options, environment.codeGenerator)
    }
}