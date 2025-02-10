package dev.sebastianb.stardriven.mixin;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void onTick(CallbackInfo ci) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        Vec3d lookVec = playerEntity.getRotationVector();
        // get the dimension registry player is in
        String dimensionRegistry = getServerWorld().getRegistryKey().getValue().toString();
        if (dimensionRegistry.startsWith("stardriven:interstellar-ship_")) {
            String shipUUID = dimensionRegistry.split("_")[1];
            DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();
            DimensionalShip dimensionalShip = dimensionalShipManager.getDimensionalShip(UUID.fromString(shipUUID));

            if (dimensionalShip != null) {
                dimensionalShip.setLogicalWorld(getServerWorld());
                if (playerEntity.isSneaking()) {
                    DimensionalStarPosition starPosition = dimensionalShip.getDimensionShipPosition();
                    // make the ship slightly move in the look vector
                    dimensionalShip
                            .setDimensionShipPosition(
                                    new DimensionalStarPosition(
                                            starPosition.getX() + lookVec.x * 0.25,
                                            starPosition.getY() + lookVec.y * 0.25,
                                            starPosition.getZ() + lookVec.z * 0.25
                                    )
                            );
                }
            }
        }
    }

}
