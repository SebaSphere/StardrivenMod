package dev.sebastianb.stardriven.networking;

import dev.sebastianb.stardriven.client.render.dimension.SpaceSkyRenderer;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.play.PlayPackets;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.network.PacketByteBuf;

public class S2CNetworking extends StardrivenNetworking {

    public static void register() {

    }

    public static void registerS2CReceiver() {


        PlayPackets.registerClientReceiver(INTERSTELLAR_SKYBOX_INITIALIZER, ((clientPlayContext, packetByteBuf) -> {
            clientPlayContext.client().execute(() -> {

                var world = clientPlayContext.handler().getWorld();
                DimensionRenderingRegistry.registerSkyRenderer(world.getRegistryKey(), SpaceSkyRenderer.INSTANCE);
            });
        }));

        PlayPackets.registerClientReceiver(INTERSTELLAR_SKYBOX_POSITION_UPDATE, ((clientPlayContext, packetByteBuf) -> {
            clientPlayContext.client().execute(() -> {
                // get from packet
                var world = clientPlayContext.handler().getWorld();
                var shipDimensionID = "stardriven:interstellar-ship_" + packetByteBuf.readUuid();
                var x = packetByteBuf.readDouble();
                var y = packetByteBuf.readDouble();
                var z = packetByteBuf.readDouble();

                // update position
                try {
                    SpaceSkyRenderer.updateShipPosition(shipDimensionID, x, y, z);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }));



    }


}
