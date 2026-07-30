package dev.fishraposo.materialprogression.stone;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

final class StrictJsonResourceLoader {
    private StrictJsonResourceLoader() {
    }

    static <T> Map<Identifier, T> load(
            ResourceManager resourceManager,
            FileToIdConverter converter,
            DynamicOps<JsonElement> ops,
            Codec<T> codec,
            String resourceKind
    ) {
        Map<Identifier, T> decoded = new LinkedHashMap<>();
        converter.listMatchingResources(resourceManager)
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Identifier fileId = entry.getKey();
                    Identifier resourceId = converter.fileToId(fileId);
                    try (var reader = entry.getValue().openAsReader()) {
                        JsonElement json = JsonParser.parseReader(reader);
                        T value = codec.parse(ops, json)
                                .getOrThrow(IllegalStateException::new);
                        T previous = decoded.putIfAbsent(resourceId, value);
                        if (previous != null) {
                            throw new IllegalStateException(
                                    "duplicate resource ID " + resourceId
                            );
                        }
                    } catch (IOException | RuntimeException exception) {
                        throw new IllegalStateException(
                                "Invalid " + resourceKind + " "
                                        + resourceId + " from " + fileId
                                        + ": " + exception.getMessage(),
                                exception
                        );
                    }
                });
        return Collections.unmodifiableMap(decoded);
    }
}
