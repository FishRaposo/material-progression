package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public final class StoneFamilyReloadListener
        extends SimpleJsonResourceReloadListener<StoneFamilyDefinition> {
    private static final Identifier LISTENER_ID = Identifier.fromNamespaceAndPath(
            MaterialProgression.MOD_ID, "stone_families"
    );
    private static volatile Map<Identifier, StoneFamilyDefinition>
            pendingDefinitions = Map.of();

    private StoneFamilyReloadListener() {
        super(
                StoneFamilyDefinition.CODEC,
                FileToIdConverter.json("stone_family")
        );
    }

    @Override
    protected void apply(
            Map<Identifier, StoneFamilyDefinition> definitions,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        pendingDefinitions = Map.copyOf(definitions);
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                StoneFamilyReloadListener::addServerReloadListener
        );
        NeoForge.EVENT_BUS.addListener(
                StoneFamilyReloadListener::tagsUpdated
        );
    }

    private static void addServerReloadListener(
            AddServerReloadListenersEvent event
    ) {
        event.addListener(LISTENER_ID, new StoneFamilyReloadListener());
    }

    private static void tagsUpdated(
            net.neoforged.neoforge.event.TagsUpdatedEvent.ServerDataLoad event
    ) {
        StoneFamilyCatalog.install(pendingDefinitions, event.getRegistries());
    }
}
