package dev.fishraposo.materialprogression.gametest;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.stone.StoneFamilyDefinition;
import dev.fishraposo.materialprogression.stone.StoneFamilyReloadListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class StoneFamilyReloadPackFixture {
    private StoneFamilyReloadPackFixture() {
    }

    static Map<Identifier, String> encode(
            Map<Identifier, StoneFamilyDefinition> definitions
    ) {
        Map<Identifier, String> resources = new LinkedHashMap<>();
        definitions.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> resources.put(
                        entry.getKey(),
                        encode(entry.getValue())
                ));
        return resources;
    }

    static String encode(StoneFamilyDefinition definition) {
        return StoneFamilyDefinition.CODEC
                .encodeStart(JsonOps.INSTANCE, definition)
                .getOrThrow(IllegalStateException::new)
                .toString();
    }

    static String withResistanceModifier(
            StoneFamilyDefinition definition,
            int modifier
    ) {
        var json = JsonParser.parseString(encode(definition))
                .getAsJsonObject();
        json.getAsJsonObject("resistance")
                .addProperty("modifier", modifier);
        return json.toString();
    }

    static void reloadAndPublish(
            ExtendedGameTestHelper helper,
            Map<Identifier, String> definitions
    ) {
        Path root = null;
        try {
            root = Files.createTempDirectory("stone-family-reload");
            for (var entry : definitions.entrySet()) {
                Identifier id = entry.getKey();
                Path definition = root.resolve(
                        "data/"
                                + id.getNamespace()
                                + "/stone_family/"
                                + id.getPath()
                                + ".json"
                );
                Files.createDirectories(definition.getParent());
                Files.writeString(definition, entry.getValue());
            }

            PackLocationInfo location = new PackLocationInfo(
                    "stone-family-reload-test",
                    Component.literal("Stone family reload test"),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );
            try (var resources = new MultiPackResourceManager(
                    PackType.SERVER_DATA,
                    List.of(new PathPackResources(location, root))
            )) {
                ReloadProbe listener = new ReloadProbe();
                var registries = helper.getLevel().registryAccess();
                listener.injectContext(
                        StoneFamilyCatalogFixture.boundTagContext(registries),
                        registries
                );
                listener.decodeAndApply(resources);
                listener.publishValidated();
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create stone-family reload pack",
                    exception
            );
        } finally {
            deleteTree(root);
        }
    }

    private static void deleteTree(Path root) {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    throw new IllegalStateException(
                            "Could not remove stone-family reload pack",
                            exception
                    );
                }
            });
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not traverse stone-family reload pack",
                    exception
            );
        }
    }

    private static final class ReloadProbe
            extends StoneFamilyReloadListener {
        private void decodeAndApply(ResourceManager resources) {
            apply(
                    prepare(resources, Profiler.get()),
                    resources,
                    Profiler.get()
            );
        }
    }
}
