package dev.sebastianb.stardriven.client;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.client.control.KeybindControl;
import dev.sebastianb.stardriven.client.render.SDDimensionEffects;
import dev.sebastianb.stardriven.networking.S2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.RenderLayer;

public class StardrivenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        SDDimensionEffects.register();
        KeybindControl.register();

        ModelLoadingPlugin.register(new StardrivenModelLoadingPlugin());

        BlockRenderLayerMap.INSTANCE.putBlock(
                StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock(),
                RenderLayer.getTranslucent());

        S2CNetworking.registerS2CReceiver();

    }
}
