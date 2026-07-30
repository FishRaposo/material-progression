package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.JsonOps;
import dev.fishraposo.materialprogression.MaterialProgression;
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

public class StoneFamilyReloadListener
        extends SimpleJsonResourceReloadListener<StoneFamilyDefinition> {
    private static final Identifier LISTENER_ID = Identifier.fromNamespaceAndPath(
            MaterialProgression.MOD_ID, "stone_families"
    );
    private static final ListenerKey<StoneFamilyReloadListener> LISTENER_KEY =
            ListenerKey.create(LISTENER_ID);
    private static final FileToIdConverter RESOURCE_CONVERTER =
            FileToIdConverter.json("stone_family");
    private StoneFamilyCatalog validatedCatalog = StoneFamilyCatalog.empty();

    @ApiStatus.Internal
    public StoneFamilyReloadListener() {
        super(
                StoneFamilyDefinition.CODEC,
                RESOURCE_CONVERTER
        );
    }

    @Override
    protected Map<Identifier, StoneFamilyDefinition> prepare(
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        return StrictJsonResourceLoader.load(
                resourceManager,
                RESOURCE_CONVERTER,
                getRegistryLookup().createSerializationContext(
                        JsonOps.INSTANCE
                ),
                StoneFamilyDefinition.CODEC,
                "stone family"
        );
    }

    @Override
    public void apply(
            Map<Identifier, StoneFamilyDefinition> definitions,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        validatedCatalog = StoneFamilyCatalog.build(
                Map.copyOf(definitions),
                getRegistryLookup(),
                getContext()
        );
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                StoneFamilyReloadListener::addServerReloadListener
        );
        NeoForge.EVENT_BUS.addListener(
                StoneFamilyReloadListener::tagsUpdated
        );
        NeoForge.EVENT_BUS.addListener(
                StoneFamilyReloadListener::serverStopped
        );
    }

    private static void addServerReloadListener(
            AddServerReloadListenersEvent event
    ) {
        event.addRetainedListener(
                LISTENER_KEY,
                new StoneFamilyReloadListener()
        );
    }

    private static void tagsUpdated(
            net.neoforged.neoforge.event.TagsUpdatedEvent.ServerDataLoad event
    ) {
        event.getServerResources()
                .getListener(LISTENER_KEY)
                .publishValidated();
    }

    private static void serverStopped(ServerStoppedEvent event) {
        StoneFamilyCatalog.clear();
    }

    @ApiStatus.Internal
    public void publishValidated() {
        StoneFamilyCatalog.install(validatedCatalog);
    }
}
