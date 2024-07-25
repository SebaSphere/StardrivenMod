package net.terradevelopment.terrautil.api_imp;

import net.minecraft.nbt.NbtCompound;
import net.terradevelopment.terrautil.api.NbtFileIO;

public enum NbtFileIOImpl implements NbtFileIO {

    INSTANCE;


    @Override
    public void writeNbtToFile(NbtCompound tag) {

    }

    @Override
    public void readNbtFromFile() {

    }

    @Override
    public NbtCompound getFileTag() {
        return null;
    }
}
