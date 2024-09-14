package dev.sebastianb.stardriven.util;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import dev.sebastianb.stardriven.block.display.DisplayBlockEntity;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static dev.sebastianb.stardriven.block.display.DisplayBlock.*;

public class DisplayUtils {

    public static class DisplayBounds implements Cloneable {
        public BlockPos min;
        public BlockPos max;

        public DisplayBounds(BlockPos min, BlockPos max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "DisplayBounds{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }

        @Override
        public DisplayBounds clone() {
            return new DisplayBounds(min, max);
        }

        public boolean containsBlock(BlockPos pos) {
            return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                    && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                    && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
        }
    }

    @NotNull
    public static BlockState getUpdatedState(Direction[] adjacentDisplayDirections, BlockState previousState) {
        if (previousState.isAir()) {
            Stardriven.LOGGER.log(Level.WARNING, "block state was air");
            return previousState;
        }

        Direction facing = previousState.get(FACING);

        Direction[] possibleDirections = getPossibleDirections(facing);

        DisplayBlock.DisplayPieceType type;
        DisplayBlock.DisplayRotation rotation;

        if (adjacentDisplayDirections.length == 0) {
            type = DisplayBlock.DisplayPieceType.SINGLE;
            rotation = DisplayBlock.DisplayRotation.R0;
        } else if (adjacentDisplayDirections.length == 1) {
            type = DisplayBlock.DisplayPieceType.THREE_EDGE;
            rotation = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
        } else if (adjacentDisplayDirections.length == 2) {
            if (adjacentDisplayDirections[0].getOpposite() == adjacentDisplayDirections[1]) {
                type = DisplayBlock.DisplayPieceType.TWO_SIDE;
                rotation = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
            } else {
                type = DisplayBlock.DisplayPieceType.CORNER;
                DisplayBlock.DisplayRotation rotation1 = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[0]);
                DisplayBlock.DisplayRotation rotation2 = rotationFromFacingAndDirection(facing, adjacentDisplayDirections[1]);

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
            type = DisplayBlock.DisplayPieceType.EDGE;

            List<Direction> adjacentDisplayDirectionsList = Arrays.asList(adjacentDisplayDirections);

            rotation = DisplayBlock.DisplayRotation.R0;

            for (Direction dir : possibleDirections) {
                if (!adjacentDisplayDirectionsList.contains(dir)) {
                    rotation = rotationFromFacingAndDirection(facing, dir).rotateClockwise();
                }
            }

            if (facing == Direction.EAST || facing == Direction.WEST || facing == Direction.DOWN) {
                rotation = rotation.rotateClockwise().rotateClockwise();
            }
        } else if (adjacentDisplayDirections.length == 4) {
            type = DisplayBlock.DisplayPieceType.EMPTY;
            rotation = DisplayBlock.DisplayRotation.R0;
        } else {
            Stardriven.LOGGER.log(Level.WARNING, "not a normal amount of displays");
            type = DisplayBlock.DisplayPieceType.SINGLE;
            rotation = DisplayBlock.DisplayRotation.R0;
        }

        return previousState
                .with(DISPLAY_ROTATION, rotation)
                .with(DISPLAY_PIECE, type);
    }

    public static DisplayRotation rotationFromFacingAndDirection(Direction facing, Direction direction) {
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

    public static Direction[] getPossibleDirections(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return new Direction[]{Direction.EAST, Direction.UP, Direction.WEST, Direction.DOWN};
        } else {
            return new Direction[]{Direction.NORTH, Direction.UP, Direction.SOUTH, Direction.DOWN};
        }
    }

    public static Direction[] getAdjacentDisplays(BlockView blockView, BlockPos blockPos, Direction[] directions, Direction facing) {
        List<Direction> adjacentDirections = new ArrayList<>();

        for (Direction dir : directions) {
            BlockPos checkPos = blockPos.offset(dir);

            BlockState blockStateCheck = blockView.getBlockState(checkPos);

            if ((blockStateCheck.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock())
                    || blockStateCheck.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY_ENTITY.asBlock()))
                    && blockStateCheck.get(FACING) == facing) {
                adjacentDirections.add(dir);
            }
        }

        return adjacentDirections.toArray(new Direction[0]);
    }

    public static ArrayList<BlockPos> getConnectedDisplays(BlockView blockView, BlockPos blockPos) {
        ArrayList<BlockPos> connectedDisplays = new ArrayList<>();
        ArrayList<BlockPos> checkedPositions = new ArrayList<>();

        DisplayUtils.getConnectedDisplays(connectedDisplays, checkedPositions, blockView, blockPos);

        connectedDisplays.add(blockPos);

        return connectedDisplays;
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

    public static List<DisplayBlockEntity> getConnectedBlockEntities(BlockView blockView, BlockPos pos) {
        var connectedDisplays = getConnectedDisplays(blockView, pos);

        return getConnectedBlockEntities(blockView, connectedDisplays);
    }

    public static ArrayList<DisplayBlockEntity> getConnectedBlockEntities(BlockView blockView, List<BlockPos> connectedDisplays) {
        ArrayList<DisplayBlockEntity> connectedBlockEntities = new ArrayList<>();

        for (var display : connectedDisplays) {
            var blockEntity = blockView.getBlockEntity(display, StardrivenBlockEntities.DISPLAY);

            blockEntity.ifPresent(connectedBlockEntities::add);

        }

        return connectedBlockEntities;
    }

    public static BlockPos[] getBlocksBetweenMinMax(BlockPos min, BlockPos max) {
        Vec3i size = max.subtract(min).add(1, 1, 1);
        BlockPos[] blocks = new BlockPos[size.getX() * size.getY() * size.getZ()];

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    blocks[x * size.getZ() * size.getY() + y * size.getZ() + z] = min.add(x, y, z);
                }
            }
        }

        return blocks;
    }

    public static BlockPos[] getBlocksInBounds(DisplayBounds bounds) {
        return getBlocksBetweenMinMax(bounds.min, bounds.max);
    }

    public static Direction.Axis getPerpendicularAxis(Direction facing, Direction.Axis axis) {
        Direction.Axis perpendicularAxis = null;

        for (Direction.Axis axisChecked : Direction.Axis.values()) {
            if (axisChecked == axis) {
                continue;
            }

            if (axisChecked == facing.getAxis()) {
                continue;
            }

            perpendicularAxis = axisChecked;
        }

        return perpendicularAxis;
    }
}
