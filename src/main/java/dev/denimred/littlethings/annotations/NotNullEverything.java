package dev.denimred.littlethings.annotations;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates that the annotated element must not be null. When applied to a class or package,
 * all members must be not-null, must return not-null, and must only accept not-null.
 * <p>
 * This behavior can be overridden by annotating nullable elements with the appropriate nullability annotation.
 * <p>
 * Note that this annotation only serves the purpose of aiding static analysis and will be ignored at runtime.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@TypeQualifierDefault({FIELD, METHOD, PARAMETER, TYPE_PARAMETER, TYPE_USE, RECORD_COMPONENT})
@NotNull //  :^)
@Nonnull // >:^)
public @interface NotNullEverything {
}
