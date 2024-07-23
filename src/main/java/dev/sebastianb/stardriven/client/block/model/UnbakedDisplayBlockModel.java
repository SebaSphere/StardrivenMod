package dev.sebastianb.stardriven.client.block.model;

import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class UnbakedDisplayBlockModel implements UnbakedModel {


    static {
        System.out.println("LOADED");
    }

    private UnbakedModel model;

    public UnbakedDisplayBlockModel(UnbakedModel unbakedModel) {
        this.model = unbakedModel;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        System.out.println("dep");

        return model.getModelDependencies();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> function) {
        System.out.println("parent");
        model.setParents(function);
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {

        return new BakedDisplayBlockModel(model.bake(baker, function, modelBakeSettings, identifier));
    }

}
