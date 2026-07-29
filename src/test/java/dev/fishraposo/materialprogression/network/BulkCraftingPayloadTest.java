package dev.fishraposo.materialprogression.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.fishraposo.materialprogression.client.ClientBulkCraftingPreviews;
import dev.fishraposo.materialprogression.testsupport.MinecraftTestBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BulkCraftingPayloadTest {
    @BeforeAll
    static void bootStrapMinecraft() {
        MinecraftTestBootstrap.bootStrap();
    }

    @Test
    void previewCodecPreservesSearchCostsAndFingerprint() {
        BulkCraftingPreviewPayload expected =
                new BulkCraftingPreviewPayload(
                        4,
                        8,
                        "minecraft:stick",
                        16,
                        32,
                        List.of("minecraft:stick", "minecraft:torch"),
                        List.of(new PreviewStack(
                                Identifier.parse("minecraft:oak_log"),
                                2
                        )),
                        List.of(),
                        List.of(new PreviewStack(
                                Identifier.parse("minecraft:oak_planks"),
                                1
                        )),
                        1234,
                        ""
                );
        ByteBuf buffer = Unpooled.buffer();

        BulkCraftingPreviewPayload.STREAM_CODEC.encode(buffer, expected);
        BulkCraftingPreviewPayload decoded =
                BulkCraftingPreviewPayload.STREAM_CODEC.decode(buffer);

        assertEquals(expected, decoded);
    }

    @Test
    void clientDiscardsOlderSearchResponse() {
        ClientBulkCraftingPreviews.clear();
        BulkCraftingPreviewPayload newer = rejected(5);
        BulkCraftingPreviewPayload older = rejected(4);

        ClientBulkCraftingPreviews.accept(newer);
        ClientBulkCraftingPreviews.accept(older);

        assertEquals(newer, ClientBulkCraftingPreviews.get(2));
        ClientBulkCraftingPreviews.clear();
    }

    private static BulkCraftingPreviewPayload rejected(long sequence) {
        return BulkCraftingPreviewPayload.rejected(
                2,
                sequence,
                List.of(),
                "",
                1,
                "test"
        );
    }
}
