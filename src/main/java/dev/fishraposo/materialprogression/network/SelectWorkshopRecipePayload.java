package dev.fishraposo.materialprogression.network;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectWorkshopRecipePayload(
        Identifier recipeId
) implements CustomPacketPayload {
    public static final Type<SelectWorkshopRecipePayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(
                    MaterialProgression.MOD_ID,
                    "select_workshop_recipe"
            )
    );
    public static final StreamCodec<ByteBuf, SelectWorkshopRecipePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    SelectWorkshopRecipePayload::recipeId,
                    SelectWorkshopRecipePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(
            SelectWorkshopRecipePayload payload,
            IPayloadContext context
    ) {
        if (context.player().containerMenu instanceof WorkshopMenu menu) {
            menu.selectRecipe(payload.recipeId());
        }
    }
}
