package net.terradevelopment.terrautil.api.file;

import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;

/**
 * Represents a contract for reading and writing NBT (Named Binary Tag) files.
 */
public interface NbtFileIO {


    /**
     * Sets the main folder path for reading and writing files.
     *
     * IMPORTANT TO SET THIS ONCE
     *
     * @param path the path to set
     */
    void setHeaderPath(Path path);

    /**
     * Returns the path of the header folder.
     *
     * @return the path of the header folder
     */
    Path getHeaderPath();

    /**
     * Sets the working path for reading and writing files.
     *
     * @param path the working path to set
     */
    void setWorkingPath(String path);

    /**
     * Retrieves the working path for file operations.
     **
     * @return the working path for file operations
     */
    Path getWorkingPath();

    void setFileIdentifier(String identifier);

    String getFileIdentifier();

    /**
     * Writes the specified NBT (Named Binary Tag) compound to a file.
     *
     * @param tag the NBT compound to write
     */
    void writeNbtToFile(NbtCompound tag);

    /**
     * Reads an NBT (Named Binary Tag) compound from a file.
     *
     * This method reads an NBT compound stored in a file and sets it as the current file tag.
     */
    void readNbtFromFile();


    /**
     * Retrieves the NBT (Named Binary Tag) compound representing the file.
     *
     * This method retrieves the NBT compound that represents the file. The NBT compound contains nested tags
     * which represent the different elements and data within the file.
     *
     * @return the NBT compound representing the file
     */
    NbtCompound getFileTag();


}
