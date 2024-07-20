package dev.sebastianb.stardriven.client.render;

import dev.sebastianb.stardriven.client.render.dimension.EmptyCloudRenderer;
import dev.sebastianb.stardriven.client.render.dimension.SpaceDimensionEffect;
import dev.sebastianb.stardriven.client.render.dimension.SpaceSkyRenderer;
import dev.sebastianb.stardriven.dimension.StardrivenDimensions;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.util.Identifier;

public class SDDimensionEffects {

    public static final Identifier SPACE = new Identifier("stardriven", "space");
    public static void register() {

        DimensionRenderingRegistry.registerDimensionEffects(SPACE, SpaceDimensionEffect.INSTANCE);
        DimensionRenderingRegistry.registerCloudRenderer(StardrivenDimensions.SPACE_WORLD_KEY, EmptyCloudRenderer.INSTANCE);
        DimensionRenderingRegistry.registerSkyRenderer(StardrivenDimensions.SPACE_WORLD_KEY, SpaceSkyRenderer.INSTANCE);

    }
}
