package dev.sebastianb.stardriven.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sebastianb.stardriven.client.render.SDDimensionEffects;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import dev.sebastianb.stardriven.client.render.dimension.star.GalaxyStarRendererManager;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class SpaceSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {

    public static final SpaceSkyRenderer INSTANCE = new SpaceSkyRenderer();

    protected final GalaxyStarRendererManager starRendererManager = new GalaxyStarRendererManager();

    @Override
    public void render(WorldRenderContext context) {

        // TODO: Add a proper sky texture
        MatrixStack matrices = new MatrixStack();

        matrices.multiplyPositionMatrix(context.positionMatrix());

        // render whole skybox black for when first loading into the dimension
        RenderSystem.setShaderColor(0.0f, 0.0F, 0.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, SDDimensionEffects.SPACE);
//        RenderSystem.setShader(GameRenderer::getPositionProgram);

        RenderSystem.disableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );


        context.profiler().push("interstellar_sky");


        matrices.push();


        starRendererManager
                .setRelativeCameraRenderPosition(new DimensionalStarPosition(0,-5,0));

        starRendererManager.setupBufferPositions();

        starRendererManager.render(context);


        matrices.pop();
        context.profiler().pop();
        RenderSystem.setShaderColor(1.0f, 1.0F, 1.0F, 1.0F);



    }
}
