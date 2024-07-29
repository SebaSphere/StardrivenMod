package dev.sebastianb.stardriven.block.controller;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;

public class ControllerBlock extends Block implements BlockEntityProvider {
    public ControllerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.isSneaking()) {
            Optional<ControllerBlockEntity> be = world.getBlockEntity(blockPos, StardrivenBlockEntities.CONTROLLER);

            if (be.isPresent()) {
                be.get().tryConnect();

                return ActionResult.SUCCESS;
            }

            Stardriven.LOGGER.log(Level.WARNING, "no block entity");
        }

        return super.onUse(blockState, world, blockPos, playerEntity, hand, blockHitResult);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerBlockEntity(blockPos, blockState);
    }
}
