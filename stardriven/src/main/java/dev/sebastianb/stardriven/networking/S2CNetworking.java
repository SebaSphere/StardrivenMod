package dev.sebastianb.stardriven.networking;

import dev.galacticraft.dynamicdimensions.impl.Constants;
import dev.sebastianb.stardriven.client.render.dimension.SpaceSkyRenderer;
import lol.bai.badpackets.api.play.PlayPackets;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;

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
    }


}
