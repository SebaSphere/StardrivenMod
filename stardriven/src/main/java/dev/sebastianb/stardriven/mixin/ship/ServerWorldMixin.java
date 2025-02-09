package dev.sebastianb.stardriven.mixin.ship;

import dev.sebastianb.stardriven.networking.StardrivenNetworking;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow public abstract ServerWorld toServerWorld();

    @Inject(method = "onDimensionChanged", at = @At("HEAD"))
    public void onPlayerTeleport(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity player) {
            triggerActionOnPlayer(player);
        }
    }

    @Inject(method = "onPlayerConnected", at = @At("HEAD"))
    public void onPlayerConnected(ServerPlayerEntity serverPlayerEntity, CallbackInfo ci) {
        triggerActionOnPlayer(serverPlayerEntity);
    }

    private void triggerActionOnPlayer(ServerPlayerEntity serverPlayerEntity) {
        var serverWorld = serverPlayerEntity.getServerWorld();

        if (serverWorld != null) {
            if (serverWorld.getRegistryKey().getValue().toTranslationKey().startsWith("stardriven.interstellar-ship_")) {
                PacketSender.s2c(serverPlayerEntity).send(
                        StardrivenNetworking.INTERSTELLAR_SKYBOX_INITIALIZER,
                        new PacketByteBuf(Unpooled.buffer()));
            }
        }
    }


}
