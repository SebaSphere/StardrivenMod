package dev.sebastianb.stardriven.client.render.dimension;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public enum EmptyCloudRenderer implements DimensionRenderingRegistry.CloudRenderer {
    INSTANCE;

    @Override
    public void render(WorldRenderContext context) {

    }

}