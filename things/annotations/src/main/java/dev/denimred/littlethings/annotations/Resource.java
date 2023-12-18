package dev.denimred.littlethings.annotations;

import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that the annotated element is a string that must follow the pattern of a {@link ResourceLocation}.
 * <p>
 * Note that this annotation only serves the purpose of aiding static analysis and will be ignored at runtime.
 *
 * @see ResourceLocation#isValidResourceLocation(String)
 */
@SuppressWarnings({"JavadocReference", "unused"})
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE})
@Pattern(ResourcePatterns.FULL)
public @interface Resource {
    /**
     * Indicates that the annotated element is a string that must follow the pattern of a {@link ResourceLocation} namespace.
     * <p>
     * Note that this annotation only serves the purpose of aiding static analysis and will be ignored at runtime.
     *
     * @see ResourceLocation#isValidNamespace(String)
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE})
    @Pattern(ResourcePatterns.NAMESPACE)
    @interface Namespace {}

    /**
     * Indicates that the annotated element is a string that must follow the pattern of a {@link ResourceLocation} path.
     * <p>
     * Note that this annotation only serves the purpose of aiding static analysis and will be ignored at runtime.
     *
     * @see ResourceLocation#isValidPath(String)
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE})
    @Pattern(ResourcePatterns.PATH)
    @interface Path {}
}

class ResourcePatterns {
    @RegExp private static final String NAMESPACE_CHARS = "[a-z0-9_.-]*";
    @RegExp static final String NAMESPACE = "^" + NAMESPACE_CHARS + "$";
    @RegExp private static final String PATH_CHARS = "[a-z0-9/._-]*";
    @RegExp static final String PATH = "^" + PATH_CHARS + "$";
    @RegExp static final String FULL = "^(" + NAMESPACE_CHARS + ":)?" + PATH_CHARS + "$";
}
