package dev.denimred.littlethings.commands.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.denimred.littlethings.annotations.Resource.Namespace;
import dev.denimred.littlethings.annotations.Resource.Path;
import net.minecraft.resources.ResourceLocation;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the JSON-deserializable information needed to define the redirect of a command.
 *
 * @param target the path of the target node to redirect to.
 * @param modifier the ID of the redirect modifier to apply, if applicable.
 * @param forks determines if the redirect forks or not. Affects how command exceptions are handled.
 */
public record JsonCommandRedirect(@Path String target, @Nullable ResourceLocation modifier, @Nullable Boolean forks) {
    public static final String TARGET = "target";
    public static final String MODIFIER = "modifier";
    public static final String FORKS = "forks";

    /**
     * Reads stored command redirect data from a given JSON element.
     *
     * @param namespace the namespace under which the command redirect is owned.
     * @param path the path of the command element that defined this redirect.
     * @param element the JSON element to read from. Must be a string or an object.
     *
     * @return the command redirect data.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static JsonCommandRedirect read(@Namespace String namespace, @Path String path, JsonElement element) {
        if (element.isJsonPrimitive()) {
            @Subst("target") var target = element.getAsString();
            return new JsonCommandRedirect(target, null, null);
        }
        var obj = element.getAsJsonObject();
        @Subst("target") var target = obj.get(TARGET).getAsString();
        var modifier = readModifier(namespace, path, obj);
        var forks = obj.has(FORKS) ? obj.get(FORKS).getAsBoolean() : null;
        return new JsonCommandRedirect(target, modifier, forks);
    }

    private static @Nullable ResourceLocation readModifier(@Namespace String namespace, @Path String path, JsonObject obj) {
        if (!obj.has(MODIFIER)) return null;
        var primitive = obj.getAsJsonPrimitive(MODIFIER);
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? new ResourceLocation(namespace, path) : null;
        } else if (primitive.isString()) {
            var str = primitive.getAsString();
            return str.indexOf(':') == -1 ? new ResourceLocation(namespace, str) : new ResourceLocation(str);
        }
        return null;
    }

    /**
     * Writes this command redirect data to a new JSON element.
     *
     * @param namespace the namespace under which the command redirect is owned.
     * @param path the path of the command element that defined this redirect.
     *
     * @return a JSON element containing the data that represents this redirect.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public JsonElement write(@Namespace String namespace, @Path String path) {
        return modifier == null && (forks == null || !forks) ? new JsonPrimitive(target) : writeFull(namespace, path);
    }

    private JsonObject writeFull(@Namespace String namespace, @Path String path) {
        var obj = new JsonObject();

        obj.addProperty(TARGET, target);

        if (modifier != null) {
            var namespaceMatches = modifier.getNamespace().equals(namespace);
            var pathMatches = modifier.getPath().equals(path);
            if (namespaceMatches && pathMatches) {
                obj.addProperty(MODIFIER, true);
            } else if (namespaceMatches) {
                obj.addProperty(MODIFIER, modifier.getPath());
            } else {
                obj.addProperty(MODIFIER, modifier.toString());
            }
        }

        if (forks != null && forks) obj.addProperty(FORKS, true);

        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonCommandRedirect that = (JsonCommandRedirect) o;
        return Objects.equals(target, that.target) && Objects.equals(modifier, that.modifier) && Objects.equals(forks, that.forks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, modifier, forks);
    }

    @Override
    public String toString() {
        return "JsonCommandRedirect{target='%s', modifier=%s, forks=%s}".formatted(target, modifier, forks);
    }
}
