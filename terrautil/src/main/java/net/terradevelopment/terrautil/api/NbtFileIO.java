package net.terradevelopment.terrautil.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface NbtFileIO {

    void writeNbtToFile(NbtCompound tag);
    void readNbtFromFile();
    NbtCompound getFileTag();


}
