package dev.sebastianb.stardriven.client.block.model;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sebastianb.stardriven.mixin.BakedQuadAccessor;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BakedDisplayBlockModel implements BakedModel, WrapperBakedModel {


    BakedModel model;
    Function<SpriteIdentifier, Sprite> textureGetter;

    public BakedDisplayBlockModel(BakedModel model, Function<SpriteIdentifier, Sprite> textureGetter) {
        this.model = model;
        this.textureGetter = textureGetter;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos blockPos, Supplier<Random> randomSupplier, RenderContext context) {

        var bakedModel = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModel(state);

        var emitter = context.getEmitter();

        model.emitBlockQuads(blockView, state, blockPos, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        List<BakedQuad> originalQuads = model.getQuads(blockState, direction, random);

        return originalQuads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return model.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return model.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return model.isBuiltin();
    }

    @Override
    public Sprite getParticleSprite() {
        return model.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return model.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return model.getOverrides();
    }

    @Override
    public @Nullable BakedModel getWrappedModel() {
        return model;
    }
}
