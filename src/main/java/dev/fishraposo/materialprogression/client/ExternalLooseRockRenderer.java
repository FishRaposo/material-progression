package dev.fishraposo.materialprogression.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
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

public final class ExternalLooseRockRenderer implements BlockEntityRenderer<
        ExternalLooseRockBlockEntity,
        ExternalLooseRockRenderState
> {
    private final ItemModelResolver itemModelResolver;

    public ExternalLooseRockRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ExternalLooseRockRenderState createRenderState() {
        return new ExternalLooseRockRenderState();
    }

    @Override
    public void extractRenderState(
            ExternalLooseRockBlockEntity rocks,
            ExternalLooseRockRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                rocks,
                state,
                partialTicks,
                cameraPosition,
                breakProgress
        );
        itemModelResolver.updateForTopItem(
                state.rock,
                rocks.rock(),
                ItemDisplayContext.GROUND,
                rocks.getLevel(),
                null,
                (int) rocks.getBlockPos().asLong()
        );
    }

    @Override
    public void submit(
            ExternalLooseRockRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (state.rock.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.08F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(25.0F));
        poseStack.scale(0.7F, 0.7F, 0.7F);
        state.rock.submit(
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
    }
}
