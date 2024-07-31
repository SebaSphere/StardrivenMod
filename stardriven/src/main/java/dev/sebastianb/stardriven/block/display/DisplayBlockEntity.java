package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import dev.sebastianb.stardriven.util.DisplayUtils;
import dev.sebastianb.stardriven.util.NbtUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;

import static dev.sebastianb.stardriven.util.DisplayUtils.getPossibleDirections;
import static dev.sebastianb.stardriven.util.DisplayUtils.getUpdatedState;

public class DisplayBlockEntity extends BlockEntity {

    private ArrayList<BlockPos> connectedDisplays;

    private BlockPos minCorner;
    private BlockPos maxCorner;

    private Direction facing;

    public DisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(StardrivenBlockEntities.DISPLAY, blockPos, blockState);

        connectedDisplays = new ArrayList<>();

        connectedDisplays.add(blockPos);

        minCorner = blockPos;
        maxCorner = blockPos;
    }

    public boolean TryConnectDisplay(ArrayList<BlockPos> availableDisplays, BlockPos addedPosition) {
        System.out.println(minCorner + " " + maxCorner);
        facing = world.getBlockState(getPos()).get(DisplayBlock.FACING);

        if (UpdateDisplays(availableDisplays)) {
            UpdateBlockstates();

            return connectedDisplays.contains(addedPosition);
        }

        return false;
    }

    private boolean UpdateDisplays(ArrayList<BlockPos> availableDisplays) {
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

        UpdateDisplays(availableDisplays);

        return true;
    }

    private void UpdateBlockstates() {
        Direction[] possibleDirections = getPossibleDirections(facing);

        for (BlockPos pos : connectedDisplays) {
            List<Direction> connectedDirections = new ArrayList<>();

            for (Direction dir : possibleDirections) {
                if (connectedDisplays.contains(pos.offset(dir))) {
                    connectedDirections.add(dir);
                }
            }

            BlockState oldBlockState = world.getBlockState(pos);

            if (oldBlockState.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY_ENTITY.asBlock()) && pos != getPos()) {
                oldBlockState = DisplayBlock.stateWithoutEntity(oldBlockState);
            }

            BlockState newBlockState = getUpdatedState(connectedDirections.toArray(new Direction[0]), oldBlockState);

            world.setBlockState(pos, newBlockState);
        }
    }

    private Vec3i getSize() {
        return maxCorner.subtract(minCorner).add(1, 1, 1);
    }

    public ArrayList<BlockPos> getConnectedDisplays() {
        return connectedDisplays;
    }

    public int size() {
        return connectedDisplays.size();
    }

    @Override
    protected void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);

        NbtUtils.putBlockPos(nbtCompound, "min_corner", minCorner);
        NbtUtils.putBlockPos(nbtCompound, "max_corner", maxCorner);
    }

    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);

        minCorner = NbtUtils.getBlockPos(nbtCompound, "min_corner");
        maxCorner = NbtUtils.getBlockPos(nbtCompound, "max_corner");

        connectedDisplays = new ArrayList<>(List.of(DisplayUtils.getBlocksBetweenMinMax(minCorner, maxCorner)));

        // maybe should do a check here to make sure all the displays actually exist
    }
}
