package dev.sebastianb.stardriven.util.ship;

import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.dimension.generator.SpaceChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.OptionalLong;
import java.util.UUID;

public class ShipCreationUtils {

    public static ServerWorld createOrLoadShipWorld(RegistryEntryLookup<Biome> biomeRegistry, DynamicDimensionRegistry dynamicDimensionRegistry, UUID shipUUID) {
        ChunkGenerator generator = new SpaceChunkGenerator(biomeRegistry);

        DimensionType type
                = new DimensionType(OptionalLong.empty(), true, false, false, true,
                1, false, false, 0, 256, 256,
                TagKey.of(Registries.BLOCK.getKey(), new Identifier(Stardriven.MOD_ID, "infiniburn_space")),
                new Identifier(Stardriven.MOD_ID, "interstellar_sky"),
                0, new DimensionType.MonsterSettings(false, true, UniformIntProvider.create(0, 7), 0));

        return dynamicDimensionRegistry.loadDynamicDimension(new Identifier(Stardriven.MOD_ID, "interstellar-ship_" + shipUUID), generator, type);

    }

}
