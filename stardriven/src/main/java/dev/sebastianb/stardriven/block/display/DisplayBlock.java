package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import static dev.sebastianb.stardriven.util.DisplayUtils.*;

public class DisplayBlock extends Block {

    public enum DisplayPieceType implements StringIdentifiable {
        CORNER("corner"),
        EDGE("edge"),
        SINGLE("single"),
        THREE_EDGE("three_edge"),
        EMPTY("empty"),
        TWO_SIDE("two_side");

        private final String name;

        DisplayPieceType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

    }

    public enum DisplayRotation implements StringIdentifiable {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        private final int rotation;

        DisplayRotation(int rotation) {
            this.rotation = rotation;
        }

        public int getRotation() {
            return rotation;
        }

        public DisplayRotation rotateClockwise() {
            int newRotation = rotation + 90;

            return DisplayRotation.fromInt(newRotation % 360);
        }

        public DisplayRotation rotateCounterClockwise() {
            int newRotation = rotation - 90;

            return DisplayRotation.fromInt(newRotation % 360);
        }

        private static DisplayRotation fromInt(int rotation) {
            switch (rotation) {
                case 0 -> {
                    return DisplayRotation.R0;
                }
                case 90 -> {
                    return DisplayRotation.R90;
                }
                case 180 -> {
                    return DisplayRotation.R180;
                }
                case 270 -> {
                    return DisplayRotation.R270;
                }
            }

            return DisplayRotation.R0;
        }

        @Override
        public String asString() {
            return String.valueOf(this.rotation);
        }

    }


    public static final EnumProperty<DisplayPieceType> DISPLAY_PIECE = EnumProperty.of("display_piece", DisplayPieceType.class);
    public static final DirectionProperty FACING = Properties.FACING;

    public static final EnumProperty<DisplayRotation> DISPLAY_ROTATION =
            EnumProperty.of("display_rotation", DisplayRotation.class);


    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 2.001, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.001, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.001);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 14.001, 16.0, 16.0, 16.0);
    protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.001, 16.0);
    protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 14.001, 0.0, 16.0, 16.0, 16.0);


    public DisplayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        switch (blockState.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case UP:
                return OPEN_BOTTOM_SHAPE;
            case DOWN:
                return OPEN_TOP_SHAPE;
        }
        return OPEN_BOTTOM_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                FACING,
                DISPLAY_PIECE,
                DISPLAY_ROTATION
        );
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        Direction facing = itemPlacementContext.getPlayerLookDirection().getOpposite();

        BlockState baseState = this.getDefaultState()
                .with(FACING, facing)
                .with(DISPLAY_PIECE, DisplayPieceType.SINGLE)
                .with(DISPLAY_ROTATION, DisplayRotation.R0);

        // return getUpdatedState(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos(), baseState);
        return baseState;
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        // Direction direction = blockState.get(FACING);
        // Direction[] directions = getPossibleDirections(direction);

        // ArrayList<BlockPos> displayPositions = new ArrayList<>();
        // displayPositions.add(blockPos);
        // getConnectedDisplays(displayPositions, new ArrayList<>(), world, blockPos, directions, direction);

        // if (displayPositions.size() == 1) {
        //     world.setBlockState(blockPos, DisplayBlockWithEntity.blockWithEntity(blockState));
        // } else {
        //     List<DisplayBlockEntity> blockEntity = findBlockEntities(world, blockPos);

        //     blockEntity.sort(Comparator.comparingInt(DisplayBlockEntity::connectedDisplayCount));

        //     if (blockEntity.isEmpty()) {
        //         Stardriven.LOGGER.log(Level.WARNING, "No blockentity found");
        //         return;
        //     }

        //     boolean updated = false;

        //     for (int entityIndex = blockEntity.size() - 1; entityIndex >= 0; entityIndex--) {
        //         if (blockEntity.get(blockEntity.size() - 1).UpdateDisplay(displayPositions)) {
        //             updated = true;
        //             break;
        //         }
        //     }

        //     if (!updated) {
        //         world.setBlockState(blockPos, DisplayBlockWithEntity.blockWithEntity(blockState));
        //     }
        // }

        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    public void onBroken(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState) {
    }
}
