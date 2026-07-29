package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

abstract class GeneratedResourceProvider implements DataProvider {
    private final String name;
    private final Path root;

    GeneratedResourceProvider(String name, Path root) {
        this.name = name;
        this.root = root;
    }

    protected abstract Map<String, JsonElement> resources();

    protected static Map<String, JsonElement> orderedResources() {
        return new TreeMap<>();
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?>[] writes = resources().entrySet().stream()
                .map(entry -> DataProvider.saveStable(
                        cache,
                        entry.getValue(),
                        root.resolve(entry.getKey())
                ))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(writes);
    }

    @Override
    public final String getName() {
        return name;
    }
}
