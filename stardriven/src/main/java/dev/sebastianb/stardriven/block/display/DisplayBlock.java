package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import dev.sebastianb.stardriven.util.DisplayUtils;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;

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
        var connectedDisplays = DisplayUtils.getConnectedDisplays(world, blockPos);

        ArrayList<DisplayBlockEntity> displayBlockEntities = DisplayUtils.getConnectedBlockEntities(world, connectedDisplays);

        displayBlockEntities.sort(Comparator.comparingInt(DisplayBlockEntity::getDisplayCount));

        boolean didConnect = false;
        ArrayList<BlockPos> consumedDisplays = new ArrayList<>();

        for (int i = displayBlockEntities.size() - 1; i >= 0; i--) {
            if (!didConnect) {
                boolean connected = displayBlockEntities.get(i).tryConnectDisplay(connectedDisplays, consumedDisplays, blockPos);

                connectedDisplays.removeAll(displayBlockEntities.get(i).getConnectedDisplays());

                if (connected) {
                    didConnect = true;
                }
            } else {
                connectedDisplays.removeAll(displayBlockEntities.get(i).getConnectedDisplays());
            }
        }

        for (BlockPos consumedDisplay : consumedDisplays) {
            for (var be : displayBlockEntities) {
                if (be.getConnectedDisplays().contains(consumedDisplay)) {
                    be.handleRemoval(consumedDisplay);
                }
            }
        }

        if (!didConnect) {
            BlockState newBlockState = DisplayWithEntity.stateWithEntity(world.getBlockState(blockPos));

            world.setBlockState(blockPos, newBlockState);
        }
    }

    @Override
    public void onBroken(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState) {
        Direction facing = blockState.get(FACING);

        ArrayList<BlockPos> connectedDisplays = new ArrayList<>();
        ArrayList<BlockPos> checkedPositions = new ArrayList<>();

        DisplayUtils.getConnectedDisplays(connectedDisplays, checkedPositions, worldAccess, blockPos,
                DisplayUtils.getPossibleDirections(facing), facing);

        connectedDisplays.add(blockPos);

        ArrayList<DisplayBlockEntity> displayBlockEntities = new ArrayList<>();

        for (BlockPos displayPos : connectedDisplays) {
            var be = worldAccess.getBlockEntity(displayPos, StardrivenBlockEntities.DISPLAY);

            if (be.isPresent()) {
                displayBlockEntities.add(be.get());
            }
        }

        for (DisplayBlockEntity displayBE : displayBlockEntities) {
            if (displayBE.getBounds().containsBlock(blockPos)) {
                displayBE.handleRemoval(blockPos);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.isSneaking()) {
            var blockEntities = DisplayUtils.getConnectedBlockEntities(world, blockPos);

            for (var be : blockEntities) {
                var pos = be.getPos().toCenterPos();

                world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 0, 0, 0);
            }

            return ActionResult.CONSUME;
        }
        return super.onUse(blockState, world, blockPos, playerEntity, hand, blockHitResult);
    }

    public static BlockState stateWithoutEntity(BlockState oldState) {
        return StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock().getDefaultState()
                .with(FACING, oldState.get(FACING))
                .with(DISPLAY_PIECE, oldState.get(DISPLAY_PIECE))
                .with(DISPLAY_ROTATION, oldState.get(DISPLAY_ROTATION));
    }
}
