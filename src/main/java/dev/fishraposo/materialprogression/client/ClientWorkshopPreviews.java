package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.network.WorkshopPreviewPayload;
import org.jspecify.annotations.Nullable;

public final class ClientWorkshopPreviews {
    private static @Nullable WorkshopPreviewPayload latest;

    private ClientWorkshopPreviews() {
    }

    public static void accept(WorkshopPreviewPayload preview) {
        if (latest == null
                || latest.containerId() != preview.containerId()
                || preview.sequence() > latest.sequence()) {
            latest = preview;
        }
    }

    public static @Nullable WorkshopPreviewPayload get(int containerId) {
        return latest != null && latest.containerId() == containerId
                ? latest
                : null;
    }

    public static void clear() {
        latest = null;
    }
}
