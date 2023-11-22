package dev.denimred.littlethings.annotations;

import net.minecraft.nbt.Tag;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static net.minecraft.nbt.Tag.*;

/**
 * Indicates that the annotated element must be a valid NBT tag type.
 *
 * @see Tag
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE, METHOD})
@MagicConstant(intValues = {TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE, TAG_BYTE_ARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INT_ARRAY, TAG_LONG_ARRAY, TAG_ANY_NUMERIC}, valuesFromClass = Tag.class)
public @interface NbtType {}
