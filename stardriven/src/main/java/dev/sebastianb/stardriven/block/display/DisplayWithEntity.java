package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import dev.sebastianb.stardriven.util.DisplayUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;

public class DisplayWithEntity extends DisplayBlock implements BlockEntityProvider {
    public DisplayWithEntity(Settings settings) {
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DisplayBlockEntity(blockPos, blockState);
    }

    public static BlockState stateWithEntity(BlockState oldState) {
        return StardrivenBlocks.DisplayBlocks.DISPLAY_ENTITY.asBlock().getDefaultState()
                .with(FACING, oldState.get(FACING))
                .with(DISPLAY_PIECE, oldState.get(DISPLAY_PIECE))
                .with(DISPLAY_ROTATION, oldState.get(DISPLAY_ROTATION));
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.isSneaking()) {
            DisplayBlockEntity blockEntity = (DisplayBlockEntity) world.getBlockEntity(blockPos);

            for (BlockPos pos : blockEntity.getConnectedDisplays()) {
                Vec3d centerPos = pos.toCenterPos();

                world.addParticle(ParticleTypes.HAPPY_VILLAGER, centerPos.x, centerPos.y, centerPos.z, 0.0, 0.0, 0.0);
            }
        }

        return super.onUse(blockState, world, blockPos, playerEntity, hand, blockHitResult);
    }
}
