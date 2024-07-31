package dev.sebastianb.stardriven.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class NbtUtils {
    public static void putBlockPos(NbtCompound tag, String identifier, BlockPos pos) {
        NbtCompound blockPosTag = new NbtCompound();

        blockPosTag.putInt("x", pos.getX());
        blockPosTag.putInt("y", pos.getY());
        blockPosTag.putInt("z", pos.getX());

        tag.put(identifier, blockPosTag);
    }

    public static BlockPos getBlockPos(NbtCompound tag, String identifier) {
        NbtCompound blockPosTag = tag.getCompound(identifier);

        int x = blockPosTag.getInt("x");
        int y = blockPosTag.getInt("y");
        int z = blockPosTag.getInt("z");

        return new BlockPos(x, y, z);
    }
}
