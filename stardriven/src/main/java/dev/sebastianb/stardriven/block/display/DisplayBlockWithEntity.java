package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class DisplayBlockWithEntity extends DisplayBlock implements BlockEntityProvider {
    public DisplayBlockWithEntity(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DisplayBlockEntity(blockPos, blockState);
    }

    public static BlockState blockWithEntity(BlockState withoutEntityState) {
        return StardrivenBlocks.DisplayBlocks.DISPLAY_WITH_ENTITY.asBlock().getDefaultState()
                .with(FACING, withoutEntityState.get(FACING))
                .with(DISPLAY_ROTATION, withoutEntityState.get(DISPLAY_ROTATION))
                .with(DISPLAY_PIECE, withoutEntityState.get(DISPLAY_PIECE));
    }
}
