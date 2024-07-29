package dev.sebastianb.stardriven.mixin.ship;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract File getRunDirectory();

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void start(CallbackInfo ci) {
        Path path = getRunDirectory().toPath();

        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();
        dimensionalShipManager.init(path);

        dimensionalShipManager.createDimensionalShip("bob", null, new DimensionalStarPosition(0, 600, 0));

    }


}