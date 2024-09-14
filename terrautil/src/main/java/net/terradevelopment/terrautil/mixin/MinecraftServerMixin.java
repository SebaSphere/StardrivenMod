package net.terradevelopment.terrautil.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract File getRunDirectory();

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void start(CallbackInfo ci) {
        Path path = getRunDirectory().toPath();


    }


}
