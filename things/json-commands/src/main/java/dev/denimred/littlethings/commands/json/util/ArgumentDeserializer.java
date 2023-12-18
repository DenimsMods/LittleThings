package dev.denimred.littlethings.commands.json.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Performs deserialization of an argument type from JSON data. */
@FunctionalInterface
public interface ArgumentDeserializer extends BiFunction<ResourceLocation, @Nullable JsonObject, ArgumentType<?>> {
    /**
     * Asserts that the provided parameters are present.
     *
     * @param typeId the argument type id, used to describe the exception.
     * @param parameters the parameters to ensure are present.
     */
    @Contract(value = "_, null -> fail", pure = true)
    static void requireParameters(ResourceLocation typeId, @Nullable JsonObject parameters) {
        if (parameters == null) throw new JsonParseException("Parameters are required for argument type: " + typeId);
    }

    /**
     * Returns the contained parameter value, otherwise a fallback.
     *
     * @param parameters the object that contains the parameter data.
     * @param name the name of the parameter to get.
     * @param fallback the value to return if the parameter is not present.
     * @param mapper the function that reads the parameter from the object.
     * @param <T> the type of value to read and return.
     *
     * @return the contained parameter, otherwise the provided fallback.
     */
    @Contract(value = "null, _, _, _ -> param3", pure = true)
    static <T> T optionalParameter(@Nullable JsonObject parameters, String name, T fallback, Function<JsonElement, T> mapper) {
        return parameters != null && parameters.has(name) ? mapper.apply(parameters.get(name)) : fallback;
    }

    @Contract(pure = true)
    @Override
    ArgumentType<?> apply(ResourceLocation typeId, @Nullable JsonObject parameters);

    /** Helper implementation of {@link ArgumentDeserializer} that defines its own type id. Mainly useful for datagen. */
    record Named(ResourceLocation typeId, ArgumentDeserializer deserializer) implements ArgumentDeserializer {
        @Override
        public ArgumentType<?> apply(ResourceLocation typeId, @Nullable JsonObject parameters) {
            return deserializer.apply(typeId, parameters);
        }
    }
}
