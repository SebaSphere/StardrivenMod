package dev.sebastianb.stardriven.dimension;

import dev.sebastianb.stardriven.dimension.generator.SpaceChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public class StardrivenDimensions {

    public static final RegistryKey<DimensionOptions> SPACE_DIMENSION_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Identifier.of("stardriven", "space"));

    public static RegistryKey<World> SPACE_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, SPACE_DIMENSION_KEY.getValue());

    public static void register() {
        Registry.register(Registries.CHUNK_GENERATOR,
                Identifier.of("stardriven", "space"),
                SpaceChunkGenerator.CODEC);

        SPACE_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("stardriven", "space"));


    }

}
