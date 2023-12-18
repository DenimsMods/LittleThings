package dev.denimred.littlethings.commands.json.util;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/** {@link MagicConstant} wrapper for named command permission levels. */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE})
@MagicConstant(valuesFromClass = LevelString.class)
public @interface LevelString {
    String ALL = "all";
    String MODERATORS = "moderators";
    String GAMEMASTERS = "gamemasters";
    String ADMINS = "admins";
    String OWNERS = "owners";
}
