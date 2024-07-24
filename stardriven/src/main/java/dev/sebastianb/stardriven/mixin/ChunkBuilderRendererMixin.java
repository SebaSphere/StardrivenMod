package dev.sebastianb.stardriven.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public class ChunkBuilderRendererMixin {


    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void translateModel(
                                float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage blockBufferBuilderStorage,
                                CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir,
                                ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData, int i, BlockPos blockPos,
                                BlockPos blockPos2, ChunkOcclusionDataBuilder chunkOcclusionDataBuilder,
                                ChunkRendererRegion chunkRendererRegion, MatrixStack matrixStack,
                                Set set, Random random, BlockRenderManager blockRenderManager,
                                Iterator var15, BlockPos blockPos3, BlockState blockState
    ) {


    }
}
