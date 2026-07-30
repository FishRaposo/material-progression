package dev.fishraposo.materialprogression.stone;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.resource.ListenerKey;
import org.jetbrains.annotations.ApiStatus;

public class GeologyDimensionProfileReloadListener
        extends SimpleJsonResourceReloadListener<
                GeologyDimensionProfileDefinition> {
    private static final Identifier LISTENER_ID =
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "geology_dimension_profile"
            );
    private static final ListenerKey<
            GeologyDimensionProfileReloadListener> LISTENER_KEY =
            ListenerKey.create(LISTENER_ID);
    private static final FileToIdConverter RESOURCE_CONVERTER =
            FileToIdConverter.json("geology_dimension_profile");

    @ApiStatus.Internal
    public GeologyDimensionProfileReloadListener() {
        super(
                GeologyDimensionProfileDefinition.CODEC,
                RESOURCE_CONVERTER
        );
    }

    @Override
    protected Map<Identifier, GeologyDimensionProfileDefinition> prepare(
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        Map<Identifier, GeologyDimensionProfileDefinition> definitions =
                new LinkedHashMap<>();
        RESOURCE_CONVERTER.listMatchingResources(resourceManager)
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Identifier fileId = entry.getKey();
                    Identifier resourceId =
                            RESOURCE_CONVERTER.fileToId(fileId);
                    try (var reader = entry.getValue().openAsReader()) {
                        var json = JsonParser.parseReader(reader);
                        GeologyDimensionProfileDefinition definition =
                                GeologyDimensionProfileDefinition.CODEC
                                        .parse(JsonOps.INSTANCE, json)
                                        .getOrThrow(IllegalStateException::new);
                        GeologyDimensionProfileDefinition previous =
                                definitions.put(resourceId, definition);
                        if (previous != null) {
                            throw new IllegalStateException(
                                    "duplicate resource ID " + resourceId
                            );
                        }
                    } catch (IOException | RuntimeException exception) {
                        throw new IllegalStateException(
                                "Invalid geology dimension profile "
                                        + resourceId
                                        + " from "
                                        + fileId
                                        + ": "
                                        + exception.getMessage(),
                                exception
                        );
                    }
                });
        return Map.copyOf(definitions);
    }

    @Override
    public void apply(
            Map<Identifier, GeologyDimensionProfileDefinition> definitions,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        GeologyDimensionProfileCatalog validated =
                GeologyDimensionProfileCatalog.build(
                        Map.copyOf(definitions)
                );
        GeologyDimensionProfileCatalog.install(validated);
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                GeologyDimensionProfileReloadListener::addReloadListener
        );
        NeoForge.EVENT_BUS.addListener(
                GeologyDimensionProfileReloadListener::serverStopped
        );
    }

    private static void addReloadListener(
            AddServerReloadListenersEvent event
    ) {
        event.addRetainedListener(
                LISTENER_KEY,
                new GeologyDimensionProfileReloadListener()
        );
    }

    private static void serverStopped(ServerStoppedEvent event) {
        GeologyDimensionProfileCatalog.clear();
    }
}
