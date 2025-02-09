package net.terradevelopment.terrautil.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.terradevelopment.terrautil.TerraUtil;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {


    @Shadow public abstract Path getRunDirectory();

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void start(CallbackInfo ci) {
        Path path = getRunDirectory().toAbsolutePath();

    }

    // TODO: also inject into the following (createLevels in mojmap)
    // target: Lnet/minecraft/server/MinecraftServer;readScoreboard(Lnet/minecraft/world/level/storage/DimensionDataStorage;)V
    // ^ mojmap

    // inject to save to world every autosave
    @Inject(method = "saveAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;saveAllPlayerData()V", ordinal = 0))
    private void saveAll(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        TerraUtil.LOGGER.log(Level.INFO, "Auto-saving all nbt data...");

        // should update all tracked files
        for (NbtFileIO nbtFileIO : NbtFileIO.getTrackedFiles()) {

            // recreates the file if it doesn't exist
            Path nbtFilePath = Paths.get(nbtFileIO.getHeaderPath() + "/" + nbtFileIO.getFileIdentifier() + ".nbt");
            File nbtFile = nbtFilePath.toFile();
            if (!nbtFile.exists()) {
                nbtFileIO.writeNbtToFile(nbtFileIO.getFileTag());
            }

            nbtFileIO.readNbtFromFile();

            System.out.println("-" + nbtFileIO.getFileTag());
            System.out.println("-" + nbtFileIO.getWorkingPath());
        }

    }


}
