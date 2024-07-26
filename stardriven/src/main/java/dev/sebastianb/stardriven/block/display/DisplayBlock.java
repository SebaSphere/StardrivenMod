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
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        // BlockState newBlockState = getUpdatedState(world, blockPos, blockState);

        // world.setBlockState(blockPos, newBlockState);

        super.neighborUpdate(blockState, world, blockPos, block, blockPos2, bl);
    }

    @NotNull
    protected static BlockState getUpdatedState(Direction[] adjacentDisplayDirections, BlockState previousState) {
        Direction facing = previousState.get(FACING);

        Direction[] possibleDirections = getPossibleDirections(facing);

        DisplayPieceType type;
        DisplayRotation rotation;

        if (adjacentDisplayDirections.length == 0) {
            type = DisplayPieceType.SINGLE;
            rotation = DisplayRotation.R0;
        } else if (adjacentDisplayDirections.length == 1) {
            type = DisplayPieceType.THREE_EDGE;
            rotation = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
        } else if (adjacentDisplayDirections.length == 2) {
            if (adjacentDisplayDirections[0].getOpposite() == adjacentDisplayDirections[1]) {
                type = DisplayPieceType.TWO_SIDE;
                rotation = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
            } else {
                type = DisplayPieceType.CORNER;
                DisplayRotation rotation1 = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
                DisplayRotation rotation2 = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[1]);

                if (rotation2.rotateClockwise() == rotation1) {
                    rotation = rotation2;
                } else {
                    rotation = rotation1;
                }

                if (facing == Direction.EAST || facing == Direction.WEST || facing == Direction.DOWN) {
                    rotation = rotation.rotateClockwise();
                }
            }
        } else if (adjacentDisplayDirections.length == 3) {
            type = DisplayPieceType.EDGE;

            List<Direction> adjacentDisplayDirectionsList = Arrays.asList(adjacentDisplayDirections);

            rotation = DisplayRotation.R0;

            for (Direction dir : possibleDirections) {
                if (!adjacentDisplayDirectionsList.contains(dir)) {
                    rotation = rotationFromFacingAndDirection(facing, dir).rotateClockwise();
                }
            }

            if (facing == Direction.EAST || facing == Direction.WEST || facing == Direction.DOWN) {
                rotation = rotation.rotateClockwise().rotateClockwise();
            }
        } else if (adjacentDisplayDirections.length == 4) {
            type = DisplayPieceType.EMPTY;
            rotation = DisplayRotation.R0;
        } else {
            Stardriven.LOGGER.log(Level.WARNING, "not a normal amount of displays");
            type = DisplayPieceType.SINGLE;
            rotation = DisplayRotation.R0;
        }

        return previousState
                .with(DISPLAY_ROTATION, rotation)
                .with(DISPLAY_PIECE, type);
    }

    private static DisplayRotation rotationFromFacingAndDirection(Direction facing, Direction direction) {
        // oh no
        // im sorry i have sinned
        switch (facing) {
            case UP, DOWN -> {
                switch (direction) {
                    case WEST -> {
                        return DisplayRotation.R0;
                    }
                    case NORTH -> {
                        return DisplayRotation.R90;
                    }
                    case SOUTH -> {
                        return DisplayRotation.R270;
                    }
                    case EAST -> {
                        return DisplayRotation.R180;
                    }
                }
            }
            case NORTH -> {
                switch (direction) {
                    case DOWN -> {
                        return DisplayRotation.R90;
                    }
                    case UP -> {
                        return DisplayRotation.R270;
                    }
                    case WEST -> {
                        return DisplayRotation.R0;
                    }
                    case EAST -> {
                        return DisplayRotation.R180;
                    }
                }
            }
            case SOUTH -> {
                switch (direction) {
                    case DOWN -> {
                        return DisplayRotation.R90;
                    }
                    case UP -> {
                        return DisplayRotation.R270;
                    }
                    case EAST -> {
                        return DisplayRotation.R0;
                    }
                    case WEST -> {
                        return DisplayRotation.R180;
                    }
                }
            }
            case WEST -> {
                switch (direction) {
                    case DOWN -> {
                        return DisplayRotation.R90;
                    }
                    case UP -> {
                        return DisplayRotation.R270;
                    }
                    case NORTH -> {
                        return DisplayRotation.R0;
                    }
                    case SOUTH -> {
                        return DisplayRotation.R180;
                    }
                }
            }
            case EAST -> {
                switch (direction) {
                    case DOWN -> {
                        return DisplayRotation.R270;
                    }
                    case UP -> {
                        return DisplayRotation.R90;
                    }
                    case NORTH -> {
                        return DisplayRotation.R0;
                    }
                    case SOUTH -> {
                        return DisplayRotation.R180;
                    }
                }
            }
        }
        Stardriven.LOGGER.log(Level.WARNING, "no rotation found for facing and direction");
        return null;
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
        Direction direction = blockState.get(FACING);
        Direction[] directions = getPossibleDirections(direction);

        ArrayList<BlockPos> displayPositions = new ArrayList<>();
        displayPositions.add(blockPos);
        getConnectedDisplays(displayPositions, new ArrayList<>(), world, blockPos, directions, direction);

        if (displayPositions.size() == 1) {
            world.setBlockState(blockPos, DisplayBlockWithEntity.blockWithEntity(blockState));
        } else {
            List<DisplayBlockEntity> blockEntity = findBlockEntities(world, blockPos);

            blockEntity.sort(Comparator.comparingInt(DisplayBlockEntity::connectedDisplayCount));

            if (blockEntity.isEmpty()) {
                Stardriven.LOGGER.log(Level.WARNING, "No blockentity found");
                return;
            }

            boolean updated = false;

            for (int entityIndex = blockEntity.size() - 1; entityIndex >= 0; entityIndex--) {
                if (blockEntity.get(blockEntity.size() - 1).UpdateDisplay(displayPositions)) {
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                world.setBlockState(blockPos, DisplayBlockWithEntity.blockWithEntity(blockState));
            }
        }

        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    public void onBroken(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState) {
    }

    protected static Direction[] getPossibleDirections(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return new Direction[]{Direction.EAST, Direction.UP, Direction.WEST, Direction.DOWN};
        } else {
            return new Direction[]{Direction.NORTH, Direction.UP, Direction.SOUTH, Direction.DOWN};
        }
    }

    protected static Direction[] getAdjacentDisplays(BlockView blockView, BlockPos blockPos, Direction[] directions, Direction facing) {
        List<Direction> adjacentDirections = new ArrayList<>();

        for (Direction dir : directions) {
            BlockPos checkPos = blockPos.offset(dir);

            BlockState blockStateCheck = blockView.getBlockState(checkPos);

            if (blockStateCheck.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock())
                    || blockStateCheck.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY_WITH_ENTITY.asBlock())) {
                if (blockStateCheck.get(FACING) == facing) {
                    adjacentDirections.add(dir);
                }
            }
        }

        return adjacentDirections.toArray(new Direction[0]);
    }

    protected static List<DisplayBlockEntity> findBlockEntities(World world, BlockPos pos) {
        ArrayList<DisplayBlockEntity> displayBlockEntities = new ArrayList<>();

        var currentBlockEntity = world.getBlockEntity(pos, StardrivenBlockEntities.DISPLAY);

        if (currentBlockEntity.isPresent()) {
            displayBlockEntities.add(currentBlockEntity.get());
        }

        ArrayList<BlockPos> possiblePositions = new ArrayList<>();
        possiblePositions.add(pos);
        getConnectedDisplays(possiblePositions, new ArrayList<>(), world, pos);

        for (BlockPos possiblePos : possiblePositions) {
            var attemptBlockEntity = world.getBlockEntity(possiblePos, StardrivenBlockEntities.DISPLAY);

            if (attemptBlockEntity.isPresent()) {
                displayBlockEntities.add(attemptBlockEntity.get());
            }
        }

        // Stardriven.LOGGER.log(Level.SEVERE, "no block entity found");
        return displayBlockEntities;
    }

    public static void getConnectedDisplays(ArrayList<BlockPos> connectedDisplays, ArrayList<BlockPos> checkedPos, BlockView blockView, BlockPos blockPos) {
        BlockState state = blockView.getBlockState(blockPos);

        Direction facing = state.get(FACING);

        Direction[] directions = getPossibleDirections(facing);

        getConnectedDisplays(connectedDisplays, checkedPos, blockView, blockPos, directions, facing);
    }

    public static void getConnectedDisplays(ArrayList<BlockPos> connectedDisplays, ArrayList<BlockPos> checkedPos, BlockView blockView, BlockPos blockPos, Direction[] directions, Direction facing) {
        Direction[] connectedDirections = getAdjacentDisplays(blockView, blockPos, directions, facing);

        if (checkedPos.contains(blockPos)) {
            return;
        }

        checkedPos.add(blockPos);

        for (Direction dir : connectedDirections) {
            BlockPos newPos = blockPos.offset(dir);

            if (checkedPos.contains(newPos)) {
                continue;
            }

            if (!connectedDisplays.contains(newPos)) {
                connectedDisplays.add(newPos);

                getConnectedDisplays(connectedDisplays, checkedPos, blockView, newPos, directions, facing);
            }
        }
    }
}
