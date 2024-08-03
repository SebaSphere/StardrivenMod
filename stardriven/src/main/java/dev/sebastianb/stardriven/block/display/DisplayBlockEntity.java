package dev.sebastianb.stardriven.block.display;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.entity.StardrivenBlockEntities;
import dev.sebastianb.stardriven.util.DisplayUtils;
import dev.sebastianb.stardriven.util.DisplayUtils.DisplayBounds;
import dev.sebastianb.stardriven.util.NbtUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

import static dev.sebastianb.stardriven.util.DisplayUtils.*;

public class DisplayBlockEntity extends BlockEntity {

    private ArrayList<BlockPos> connectedDisplays;

    private DisplayBounds bounds;

    private Direction facing;

    public DisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(StardrivenBlockEntities.DISPLAY, blockPos, blockState);

        connectedDisplays = new ArrayList<>();

        connectedDisplays.add(blockPos);

        bounds = new DisplayBounds(blockPos, blockPos);

        facing = blockState.get(DisplayBlock.FACING);
    }

    DisplayBlockEntity(BlockPos blockPos, BlockState blockState, DisplayBounds bounds) {
        super(StardrivenBlockEntities.DISPLAY, blockPos, blockState);

        facing = blockState.get(DisplayBlock.FACING);

        this.bounds = bounds;

        connectedDisplays = new ArrayList<>(List.of(getBlocksInBounds(bounds)));
    }

    public boolean tryConnectDisplay(ArrayList<BlockPos> availableDisplays, ArrayList<BlockPos> consumedDisplays, BlockPos addedPosition) {
        if (updateDisplays(availableDisplays, consumedDisplays)) {
            updateBlockstates();

            return connectedDisplays.contains(addedPosition);
        }

        return false;
    }

    private boolean updateDisplays(ArrayList<BlockPos> availableDisplays, ArrayList<BlockPos> consumedDisplays) {
        Direction[] possibleDirections = getPossibleDirections(facing);

        Vec3i size = getSize();

        Map<Direction, ArrayList<BlockPos>> expansionSizes = new HashMap<>();

        for (Direction dir : possibleDirections) {

            int targetCoord = bounds.min.offset(dir).getComponentAlongAxis(dir.getAxis());
            if (dir.getDirection() == Direction.AxisDirection.POSITIVE) {
                targetCoord = bounds.max.offset(dir).getComponentAlongAxis(dir.getAxis());
            }

            var newMin = bounds.min.offset(dir.getAxis(), targetCoord - bounds.min.getComponentAlongAxis(dir.getAxis()));

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

            if (!consumedDisplays.contains(pos)) {
                consumedDisplays.add(pos);
            }
        }

        if (expansionDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
            bounds.max = bounds.max.offset(expansionDirection);
        } else {
            bounds.min = bounds.min.offset(expansionDirection);
        }

        updateDisplays(availableDisplays, consumedDisplays);

        return true;
    }

    public void handleRemoval(BlockPos brokenBlock) {
        Vec3i offset = brokenBlock.subtract(bounds.min);
        Vec3i size = getSize();

        Vec3i otherOffset = offset.subtract(size);

        if (otherOffset.getX() > 0 || otherOffset.getY() > 0 || otherOffset.getZ() > 0) {
            // block outside display ??????
            return;
        }

        Vec3i negativeOffset = brokenBlock.subtract(bounds.max);

        Direction[] possibleDirections = getPossibleDirections(facing);

        HashMap<Direction, Integer> offsets = new HashMap<>();

        for (Direction dir : possibleDirections) {
            int directionOffset;

            if (dir.getDirection() == Direction.AxisDirection.POSITIVE) {
                directionOffset = Math.abs(negativeOffset.getComponentAlongAxis(dir.getAxis()));
            } else {
                directionOffset = Math.abs(offset.getComponentAlongAxis(dir.getAxis()));
            }

            offsets.put(dir, directionOffset);
        }

        var sortedOffsets = offsets.entrySet().stream().sorted(Comparator.comparingInt(v -> {
            int perpendicularSize = size.getComponentAlongAxis(getPerpendicularAxis(facing, v.getKey().getAxis()));
            return (v.getValue() + 1) * perpendicularSize;
        })).toList();

        DisplayBounds[] boundsArray = new DisplayBounds[4];

        var minOffset = sortedOffsets.get(0);

        var firstOffsetDirection = minOffset.getKey().getOpposite();
        var firstOffsetAmount = offsets.get(firstOffsetDirection);

        if (firstOffsetAmount != 0) {
            var mainBounds = bounds.clone();

            if (firstOffsetDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                mainBounds.min = mainBounds.min.offset(firstOffsetDirection, minOffset.getValue() + 1);
            } else {
                mainBounds.max = mainBounds.max.offset(firstOffsetDirection, minOffset.getValue() + 1);
            }

            boundsArray[0] = mainBounds;
        }

        var secondOffsetAxis = getPerpendicularAxis(facing, firstOffsetDirection.getAxis());

        var secondOffsetDirection = sortedOffsets.stream().filter(v -> v.getKey().getAxis() == secondOffsetAxis).toList().get(0).getKey();
        var secondOffsetAmount = offsets.get(secondOffsetDirection);

        if (secondOffsetAmount != 0) {
            boundsArray[1] = new DisplayBounds(brokenBlock.offset(secondOffsetDirection), brokenBlock.offset(secondOffsetDirection));

            if (secondOffsetDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                boundsArray[1].max = boundsArray[1].max.offset(secondOffsetDirection, secondOffsetAmount - 1);
            } else {
                boundsArray[1].min = boundsArray[1].min.offset(secondOffsetDirection, secondOffsetAmount - 1);
            }

            if (minOffset.getKey().getDirection() == Direction.AxisDirection.POSITIVE) {
                boundsArray[1].max = boundsArray[1].max.offset(minOffset.getKey(), minOffset.getValue());
            } else {
                boundsArray[1].min = boundsArray[1].min.offset(minOffset.getKey(), minOffset.getValue());
            }
        }

        var thirdOffsetDirection = secondOffsetDirection.getOpposite();
        var thirdOffsetAmount = offsets.get(thirdOffsetDirection);

        if (thirdOffsetAmount != 0) {
            boundsArray[2] = new DisplayBounds(brokenBlock.offset(thirdOffsetDirection), brokenBlock.offset(thirdOffsetDirection));

            if (thirdOffsetDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                boundsArray[2].max = boundsArray[2].max.offset(thirdOffsetDirection, thirdOffsetAmount - 1);
            } else {
                boundsArray[2].min = boundsArray[2].min.offset(thirdOffsetDirection, thirdOffsetAmount - 1);
            }

            if (minOffset.getKey().getDirection() == Direction.AxisDirection.POSITIVE) {
                boundsArray[2].max = boundsArray[2].max.offset(minOffset.getKey(), minOffset.getValue());
            } else {
                boundsArray[2].min = boundsArray[2].min.offset(minOffset.getKey(), minOffset.getValue());
            }
        }

        var fourthOffsetDirection = minOffset.getKey();
        var fourthOffsetAmount = offsets.get(fourthOffsetDirection);

        if (fourthOffsetAmount != 0) {
            boundsArray[3] = new DisplayBounds(brokenBlock.offset(fourthOffsetDirection), brokenBlock.offset(fourthOffsetDirection));

            if (fourthOffsetDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                boundsArray[3].max = boundsArray[3].max.offset(fourthOffsetDirection, fourthOffsetAmount - 1);
            } else {
                boundsArray[3].min = boundsArray[3].min.offset(fourthOffsetDirection, fourthOffsetAmount - 1);
            }
        }

        List<DisplayBlockEntity> newDisplays = new ArrayList<>();

        for (DisplayBounds bounds : boundsArray) {
            if (bounds != null) {
                var newDisplay = createNewScreen(bounds);

                newDisplays.add(newDisplay);
            }
        }

        var availableDisplays = DisplayUtils.getConnectedDisplays(world, pos);
        var connectedDisplayEntities = getConnectedBlockEntities(world, availableDisplays);

        connectedDisplayEntities.sort(Comparator.comparingInt(DisplayBlockEntity::getDisplayCount));

        for (var display : connectedDisplayEntities.get(connectedDisplayEntities.size() - 1).connectedDisplays) {
            var pos = display.toCenterPos();

            world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 0, 0, 0);
        }

        ArrayList<BlockPos> consumedDisplays = new ArrayList<>();

        for (int i = connectedDisplayEntities.size() - 1; i >= 0; i--) {
            if (world.getBlockState(connectedDisplayEntities.get(i).getPos()).isOf(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock())
                || connectedDisplayEntities.get(i).getPos().equals(brokenBlock)) {
                continue;
            }

            connectedDisplayEntities.get(i).updateDisplays(availableDisplays, consumedDisplays);

            availableDisplays.removeAll(connectedDisplayEntities.get(i).getConnectedDisplays());

            connectedDisplayEntities.get(i).updateBlockstates();
        }
    }

    private DisplayBlockEntity createNewScreen(DisplayBounds bounds) {
        if (bounds.containsBlock(pos)) {
            this.bounds = bounds;

            connectedDisplays = new ArrayList<>(List.of(getBlocksInBounds(bounds)));

            updateBlockstates();

            return this;
        }

        BlockState oldMinState = world.getBlockState(bounds.min);

        BlockState newMinState = DisplayWithEntity.stateWithEntity(oldMinState);

        DisplayBlockEntity newEntity = new DisplayBlockEntity(bounds.min, newMinState, bounds);

        world.setBlockState(bounds.min, newMinState);

        world.addBlockEntity(newEntity);

        world.updateListeners(pos, newMinState, newMinState, Block.NOTIFY_LISTENERS);

        newEntity.updateBlockstates();

        return newEntity;
    }

    private void updateBlockstates() {
        Direction[] possibleDirections = getPossibleDirections(facing);

        for (BlockPos pos : connectedDisplays) {
            List<Direction> connectedDirections = new ArrayList<>();

            for (Direction dir : possibleDirections) {
                if (connectedDisplays.contains(pos.offset(dir))) {
                    connectedDirections.add(dir);
                }
            }

            BlockState oldBlockState = world.getBlockState(pos);

            if (oldBlockState.isOf(StardrivenBlocks.DisplayBlocks.DISPLAY_ENTITY.asBlock()) && !pos.equals(getPos())) {
                oldBlockState = DisplayBlock.stateWithoutEntity(oldBlockState);
            }

            BlockState newBlockState = getUpdatedState(connectedDirections.toArray(new Direction[0]), oldBlockState);

            world.setBlockState(pos, newBlockState);
        }
    }

    private Vec3i getSize() {
        return bounds.max.subtract(bounds.min).add(1, 1, 1);
    }

    public ArrayList<BlockPos> getConnectedDisplays() {
        return connectedDisplays;
    }

    public int getDisplayCount() {
        return connectedDisplays.size();
    }

    public BlockPos getMin() {
        return bounds.min;
    }

    public BlockPos getMax() {
        return bounds.max;
    }

    public DisplayBounds getBounds() {
        return bounds;
    }

    @Override
    protected void writeNbt(NbtCompound nbtCompound) {
        NbtUtils.putBlockPos(nbtCompound, "min_corner", bounds.min);
        NbtUtils.putBlockPos(nbtCompound, "max_corner", bounds.max);

        super.writeNbt(nbtCompound);
    }

    @Override
    public void readNbt(NbtCompound nbtCompound) {
        bounds.min = NbtUtils.getBlockPos(nbtCompound, "min_corner");
        bounds.max = NbtUtils.getBlockPos(nbtCompound, "max_corner");

        connectedDisplays = new ArrayList<>(List.of(DisplayUtils.getBlocksInBounds(bounds)));

        // maybe should do a check here to make sure all the displays actually exist

        super.readNbt(nbtCompound);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
