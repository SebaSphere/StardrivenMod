package dev.sebastianb.stardriven.client;

import dev.sebastianb.stardriven.client.block.model.BakedDisplayBlockModel;
import dev.sebastianb.stardriven.client.block.model.UnbakedDisplayBlockModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class StardrivenModelLoadingPlugin implements ModelLoadingPlugin {

    public static final Identifier DISPLAY_MODEL = new Identifier("stardriven", "block/display");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {

        pluginContext.modifyModelAfterBake().register((loaded, context) -> {
            if(context.id().toTranslationKey().startsWith(DISPLAY_MODEL.toTranslationKey())) {
                return loaded;
            } else {
                return loaded;
            }
        });
    }

}
