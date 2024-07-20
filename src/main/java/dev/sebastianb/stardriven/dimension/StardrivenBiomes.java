package dev.sebastianb.stardriven.dimension;

import dev.sebastianb.stardriven.dimension.generator.SpaceChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class StardrivenBiomes {

    public static final RegistryKey<Biome> SPACE = RegistryKey.of(RegistryKeys.BIOME, new Identifier("stardriven", "space"));

    public static void register() {


    }



}
