package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.network.BulkCraftingPreviewPayload;
import org.jspecify.annotations.Nullable;

public final class ClientBulkCraftingPreviews {
    private static @Nullable BulkCraftingPreviewPayload latest;

    private ClientBulkCraftingPreviews() {
    }

    public static void accept(BulkCraftingPreviewPayload preview) {
        if (latest == null
                || latest.containerId() != preview.containerId()
                || preview.sequence() > latest.sequence()) {
            latest = preview;
        }
    }

    public static @Nullable BulkCraftingPreviewPayload get(
            int containerId
    ) {
        return latest != null && latest.containerId() == containerId
                ? latest
                : null;
    }

    public static void clear() {
        latest = null;
    }
}
