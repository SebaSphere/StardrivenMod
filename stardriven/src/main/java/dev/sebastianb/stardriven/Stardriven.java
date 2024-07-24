package dev.sebastianb.stardriven;

import dev.sebastianb.stardriven.api.StardrivenAPI;
import dev.sebastianb.stardriven.api.impl.StardrivenAPIImpl;
import dev.sebastianb.stardriven.dimension.StardrivenBiomes;
import dev.sebastianb.stardriven.dimension.StardrivenDimensions;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import net.fabricmc.api.ModInitializer;
import net.terradevelopment.terrautil.util.ModRegistry;

import java.util.logging.Logger;

public class Stardriven implements ModInitializer {

    public static String MOD_ID = "stardriven";

    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static final ModRegistry REGISTRY = new ModRegistry(MOD_ID);

    /**
     * Runs the mod initializer.
     *
     */
    @Override
    public void onInitialize() {

        StardrivenAPI._init(StardrivenAPIImpl.INSTANCE);



        StardrivenDimensions.register();
        StardrivenBiomes.register();
        StardrivenBlocks.register();

    }
}
