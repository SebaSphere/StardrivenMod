package dev.sebastianb.stardriven.mixin;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"))
    private void renderBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random, CallbackInfo ci) {

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
