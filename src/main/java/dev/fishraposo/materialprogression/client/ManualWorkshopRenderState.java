package dev.fishraposo.materialprogression.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public final class ManualWorkshopRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState tool = new ItemStackRenderState();
}
