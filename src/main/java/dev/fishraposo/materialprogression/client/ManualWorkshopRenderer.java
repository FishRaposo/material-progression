package dev.fishraposo.materialprogression.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.fishraposo.materialprogression.world.level.block.entity.ManualWorkshopBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class ManualWorkshopRenderer implements BlockEntityRenderer<
        ManualWorkshopBlockEntity,
        ManualWorkshopRenderState
> {
    private final ItemModelResolver itemModelResolver;

    public ManualWorkshopRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ManualWorkshopRenderState createRenderState() {
        return new ManualWorkshopRenderState();
    }

    @Override
    public void extractRenderState(
            ManualWorkshopBlockEntity workshop,
            ManualWorkshopRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                workshop,
                state,
                partialTicks,
                cameraPosition,
                breakProgress
        );
        itemModelResolver.updateForTopItem(
                state.tool,
                workshop.getItem(ManualWorkshopBlockEntity.TOOL_SLOT),
                ItemDisplayContext.FIXED,
                workshop.getLevel(),
                null,
                (int) workshop.getBlockPos().asLong()
        );
    }

    @Override
    public void submit(
            ManualWorkshopRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (state.tool.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.03F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(35.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        state.tool.submit(
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
    }
}
