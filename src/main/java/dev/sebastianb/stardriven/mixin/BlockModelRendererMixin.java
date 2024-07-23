package dev.sebastianb.stardriven.mixin;

import dev.sebastianb.stardriven.client.DisplayBlockRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @Inject(method = "renderFlat",
            at = @At("HEAD"))
    private void render(
            BlockRenderView blockRenderView, BakedModel bakedModel,
            BlockState blockState, BlockPos blockPos, MatrixStack matrixStack,
            VertexConsumer vertexConsumer, boolean bl, Random random,
            long l, int i, CallbackInfo ci
    ) {
        DisplayBlockRenderer.renderFlat(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, bl, random, l, i, ci);

    }

}
