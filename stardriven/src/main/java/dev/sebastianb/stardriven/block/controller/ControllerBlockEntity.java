package dev.sebastianb.stardriven.block.controller;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import dev.sebastianb.stardriven.util.DisplayUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.*;
import java.util.logging.Level;

import static dev.sebastianb.stardriven.util.DisplayUtils.getPossibleDirections;
import static dev.sebastianb.stardriven.util.DisplayUtils.getUpdatedState;

public class ControllerBlockEntity extends BlockEntity {

    private ArrayList<BlockPos> connectedDisplays;

    private BlockPos minCorner;
    private BlockPos maxCorner;

    private Direction facing;

    public ControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(StardrivenBlockEntities.CONTROLLER, blockPos, blockState);
    }

    protected void tryConnect() {
        for (Direction dir : Direction.values()) {
            BlockState state = getWorld().getBlockState(getPos().offset(dir));
            if (state.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock())) {
                facing = state.get(DisplayBlock.FACING);

                ConnectDisplays(getPos().offset(dir));
            }
        }
    }

    private void ConnectDisplays(BlockPos basePos) {
        connectedDisplays = new ArrayList<>();

        connectedDisplays.add(basePos);

        minCorner = basePos;
        maxCorner = basePos;

        ArrayList<BlockPos> displays = new ArrayList<>();
        ArrayList<BlockPos> checkedPos = new ArrayList<>();

        DisplayUtils.getConnectedDisplays(displays, checkedPos, getWorld(), basePos);

        UpdateDisplays(displays);
    }

    public boolean UpdateDisplays(ArrayList<BlockPos> availableDisplays) {
        Direction[] possibleDirections = getPossibleDirections(facing);

        Vec3i size = getSize();

        Map<Direction, ArrayList<BlockPos>> expansionSizes = new HashMap<>();

        for (Direction dir : possibleDirections) {

            int targetCoord = minCorner.offset(dir).getComponentAlongAxis(dir.getAxis());
            if (dir.getDirection() == Direction.AxisDirection.POSITIVE) {
                targetCoord = maxCorner.offset(dir).getComponentAlongAxis(dir.getAxis());
            }

            var newMin = minCorner.offset(dir.getAxis(), targetCoord - minCorner.getComponentAlongAxis(dir.getAxis()));

            Direction.Axis checkAxis = null;

            for (Direction.Axis axis : Direction.Axis.values()) {
                if (axis == dir.getAxis()) {
                    continue;
                }

                if (axis == facing.getAxis()) {
                    continue;
                }

                checkAxis = axis;
            }

            if (checkAxis == null) {
                Stardriven.LOGGER.log(Level.WARNING, "no axis found");
            }

            ArrayList<BlockPos> possesToAdd = new ArrayList<>();

            for (int offset = 0; offset < size.getComponentAlongAxis(checkAxis); offset++) {
                BlockPos checkPos = newMin.offset(checkAxis, offset);

                if (!availableDisplays.contains(checkPos)) {
                    possesToAdd.clear();
                    break;
                }

                possesToAdd.add(checkPos);
            }

            expansionSizes.put(dir, possesToAdd);
        }

        Direction expansionDirection = expansionSizes.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().size())).get().getKey();

        if (expansionSizes.get(expansionDirection).size() == 0) {
            return false;
        }

        for (BlockPos pos : expansionSizes.get(expansionDirection)) {
            if (!connectedDisplays.contains(pos)) {
                connectedDisplays.add(pos);
            }
        }

        if (expansionDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            maxCorner = maxCorner.offset(expansionDirection);
        } else {
            minCorner = minCorner.offset(expansionDirection);
        }

        if (!UpdateDisplays(availableDisplays)) {
            UpdateBlockstates();
        }

        return true;
    }

    private void UpdateBlockstates() {
        Direction[] possibleDirections = getPossibleDirections(facing);

        System.out.println(connectedDisplays);

        for (BlockPos pos : connectedDisplays) {
            List<Direction> connectedDirections = new ArrayList<>();

            for (Direction dir : possibleDirections) {
                if (connectedDisplays.contains(pos.offset(dir))) {
                    connectedDirections.add(dir);
                }
            }

            BlockState oldBlockState = world.getBlockState(pos);

            BlockState newBlockState = getUpdatedState(connectedDirections.toArray(new Direction[0]), oldBlockState);

            System.out.println(pos + " " + newBlockState + " " + Arrays.toString(connectedDirections.toArray()));

            world.setBlockState(pos, newBlockState);
        }
    }

    private Vec3i getSize() {
        return maxCorner.subtract(minCorner).add(1, 1, 1);
    }

    private Vec3i calculateOffset(BlockPos pos) {
        int xOffset = 0;

        if (minCorner.getX() - pos.getX() > 0) {
            xOffset = -(minCorner.getX() - pos.getX());
        }

        if (maxCorner.getX() - pos.getX() < 0) {
            xOffset = -(maxCorner.getX() - pos.getX());
        }

        int yOffset = 0;

        if (minCorner.getY() - pos.getY() > 0) {
            yOffset = -(minCorner.getY() - pos.getY());
        }

        if (maxCorner.getY() - pos.getY() < 0) {
            yOffset = -(maxCorner.getY() - pos.getY());
        }

        int zOffset = 0;

        if (minCorner.getZ() - pos.getZ() > 0) {
            zOffset = -(minCorner.getZ() - pos.getZ());
        }

        if (maxCorner.getZ() - pos.getZ() < 0) {
            zOffset = -(maxCorner.getZ() - pos.getZ());
        }

        return new Vec3i(xOffset, yOffset, zOffset);
    }
}
