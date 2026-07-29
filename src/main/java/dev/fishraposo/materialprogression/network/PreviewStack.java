package dev.fishraposo.materialprogression.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record PreviewStack(Identifier itemId, int count) {
    private static final StreamCodec<ByteBuf, Identifier> IDENTIFIER_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    Identifier::parse,
                    Identifier::toString
            );
    public static final PreviewStack EMPTY = new PreviewStack(
            BuiltInRegistries.ITEM.getKey(Items.AIR),
            0
    );
    public static final StreamCodec<ByteBuf, PreviewStack> STREAM_CODEC =
            StreamCodec.composite(
                    IDENTIFIER_CODEC,
                    PreviewStack::itemId,
                    ByteBufCodecs.VAR_INT,
                    PreviewStack::count,
                    PreviewStack::new
            );

    public PreviewStack {
        if (count < 0) {
            throw new IllegalArgumentException(
                    "Preview stack count cannot be negative"
            );
        }
    }

    public static PreviewStack from(ItemStack stack) {
        return stack.isEmpty()
                ? EMPTY
                : new PreviewStack(
                        BuiltInRegistries.ITEM.getKey(stack.getItem()),
                        stack.getCount()
                );
    }

    public ItemStack toStack() {
        return count == 0
                ? ItemStack.EMPTY
                : new ItemStack(
                        BuiltInRegistries.ITEM.getValue(itemId),
                        count
                );
    }
}
