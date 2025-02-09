package dev.sebastianb.stardriven.networking;

import dev.sebastianb.stardriven.client.render.dimension.SpaceSkyRenderer;
import lol.bai.badpackets.api.play.PlayPackets;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;

public class S2CNetworking extends StardrivenNetworking {

    public static void register() {

    }

    public static void registerS2CReceiver() {
        PlayPackets.registerClientReceiver(INTERSTELLAR_SKYBOX_INITIALIZER, ((context, payload) -> {
            context.client().execute(() -> {
                var world = context.client().world;
                DimensionRenderingRegistry.registerSkyRenderer(world.getRegistryKey(), SpaceSkyRenderer.INSTANCE);
            });
        }));
    }


}
