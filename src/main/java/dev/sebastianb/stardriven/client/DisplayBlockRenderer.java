package dev.sebastianb.stardriven.client;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Set;

public class DisplayBlockRenderer {

    public static void renderFlat(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random, CallbackInfo ci) {

        if (blockState.getBlock().equals(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock())) {

        }
    }



    public static void renderFlat(
            BlockRenderView blockRenderView, BakedModel bakedModel,
            BlockState blockState, BlockPos blockPos, MatrixStack matrixStack,
            VertexConsumer vertexConsumer, boolean bl, Random random,
            long l, int i, CallbackInfo ci
    ) {

    }

    public static void translateModel(
            float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage blockBufferBuilderStorage,
            CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir,
            ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData, int i, BlockPos blockPos,
            BlockPos blockPos2, ChunkOcclusionDataBuilder chunkOcclusionDataBuilder,
            ChunkRendererRegion chunkRendererRegion, MatrixStack matrixStack,
            Set set, Random random, BlockRenderManager blockRenderManager,
            Iterator var15, BlockPos blockPos3, BlockState blockState
    ) {
        if (blockState.getBlock() == StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock()) {

            int rotation = blockState.get(DisplayBlock.DISPLAY_ROTATION).getRotation();

            Direction direction = blockState.get(DisplayBlock.FACING);
            switch (direction) {
                case EAST -> {



                    matrixStack.translate(0.5, 0.5, 0.5);
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation));
                    matrixStack.translate(-0.5, -0.5, -0.5);
                }
            }


        }
    }

}
