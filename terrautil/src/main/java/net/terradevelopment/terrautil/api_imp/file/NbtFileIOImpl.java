package net.terradevelopment.terrautil.api_imp.file;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.terradevelopment.terrautil.api.file.NbtFileIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum NbtFileIOImpl implements NbtFileIO {

    INSTANCE;


    private Path headerPath;
    private Path workingPath;

    private NbtCompound fileTag = new NbtCompound();

    private String fileIdentifier;

    /**
     *
     * IT'S IMPORTANT TO SET THIS AT LEAST ONCE BEFORE USING ANY OTHER METHODS
     *
     * @param path the header path to set
     */
    @Override
    public void setHeaderPath(Path path) {
        if (this.headerPath == null) {
            this.headerPath = path;
            this.workingPath = path;
        } else {
            // throw new IllegalStateException("Path already set");
        }
    }

    @Override
    public Path getHeaderPath() {
        return headerPath;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    @Override
    public void setWorkingPath(String path) {
        workingPath = headerPath.resolve(path);
        if (!Files.exists(workingPath)) {
            try {
                Files.createDirectories(workingPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Path getWorkingPath() {
        return workingPath;
    }




    @Override
    public void writeNbtToFile(NbtCompound tag) {
        try {

            // Update the existing tag with the new data
            tag.copyFrom(fileTag);

            // Write the updated tag back to the file
            NbtIo.write(tag, getWorkingPath().resolve(fileIdentifier + ".nbt"));


        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    @Override
    public void readNbtFromFile() {

    }

    @Override
    public NbtCompound getFileTag() {
        return null;
    }
}
