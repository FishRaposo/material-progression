package dev.fishraposo.materialprogression.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.fishraposo.materialprogression.client.ClientWorkshopPreviews;
import dev.fishraposo.materialprogression.testsupport.MinecraftTestBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WorkshopPayloadTest {
    @BeforeAll
    static void bootStrapMinecraft() {
        MinecraftTestBootstrap.bootStrap();
    }

    @Test
    void previewStreamCodecPreservesTheCompleteImmutableSummary() {
        WorkshopPreviewPayload expected = new WorkshopPreviewPayload(
                7,
                12,
                Identifier.parse(
                        "material_progression:manual_processing/knife_rock"
                ),
                4,
                3,
                PreviewStack.from(Items.COBBLESTONE.getDefaultInstance()
                        .copyWithCount(3)),
                PreviewStack.from(Items.FLINT.getDefaultInstance()
                        .copyWithCount(6)),
                3,
                List.of(PreviewStack.from(
                        Items.BUCKET.getDefaultInstance().copyWithCount(3)
                )),
                42,
                "insufficient_input"
        );
        ByteBuf buffer = Unpooled.buffer();

        WorkshopPreviewPayload.STREAM_CODEC.encode(buffer, expected);
        WorkshopPreviewPayload decoded =
                WorkshopPreviewPayload.STREAM_CODEC.decode(buffer);

        assertEquals(expected, decoded);
        assertTrue(decoded.consumed().toStack().is(Items.COBBLESTONE));
        assertEquals(6, decoded.produced().count());
    }

    @Test
    void clientIgnoresOutOfOrderPreviewResponses() {
        ClientWorkshopPreviews.clear();
        WorkshopPreviewPayload newer = previewWithSequence(4);
        WorkshopPreviewPayload older = previewWithSequence(3);

        ClientWorkshopPreviews.accept(newer);
        ClientWorkshopPreviews.accept(older);

        assertEquals(newer, ClientWorkshopPreviews.get(9));
        ClientWorkshopPreviews.clear();
    }

    private static WorkshopPreviewPayload previewWithSequence(
            long sequence
    ) {
        return WorkshopPreviewPayload.rejected(
                9,
                sequence,
                Identifier.parse(
                        "material_progression:manual_processing/knife_rock"
                ),
                1,
                2,
                "test"
        );
    }
}
