package dev.denimred.littlethings.commands.json.util;

import net.minecraft.commands.Commands;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.denimred.littlethings.commands.json.util.LevelInt.*;
import static java.lang.annotation.ElementType.*;

/** {@link MagicConstant} wrapper for integer command permission levels. */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE})
@MagicConstant(intValues = {ALL, MODERATORS, GAMEMASTERS, ADMINS, OWNERS}, valuesFromClass = Commands.class)
public @interface LevelInt {
    int ALL = Commands.LEVEL_ALL;
    int MODERATORS = Commands.LEVEL_MODERATORS;
    int GAMEMASTERS = Commands.LEVEL_GAMEMASTERS;
    int ADMINS = Commands.LEVEL_ADMINS;
    int OWNERS = Commands.LEVEL_OWNERS;
}
