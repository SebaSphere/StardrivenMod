package dev.sebastianb.stardriven.dimension.generator;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sebastianb.stardriven.dimension.StardrivenBiomes;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BlockState;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.minecraft.server.command.PlaceCommand.throwOnUnloadedPos;

public class SpaceChunkGenerator extends ChunkGenerator {

    public static final Codec<SpaceChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME))
                    .apply(instance, instance.stable(SpaceChunkGenerator::new)));

    public SpaceChunkGenerator(RegistryEntryLookup<Biome> biomeRegistry) {
        super(new FixedBiomeSource(biomeRegistry.getOrThrow(StardrivenBiomes.SPACE)));
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long l, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carver) {

    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor accessor) {

//        super.generateFeatures(world, chunk, accessor);
//        ChunkPos chunkPos = chunk.getPos();
//        BlockPos pos = new BlockPos(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ());
//        int startX = chunkPos.getStartX();
//        int startZ = chunkPos.getStartZ();
//
//        if (chunkPos.x == 0 && chunkPos.z == 0) {
//            ServerChunkManager chunkManager = world.toServerWorld().getChunkManager();
//
//            Structure structure = accessor.getRegistryManager().get(RegistryKeys.STRUCTURE).get(StructureKeys.VILLAGE_DESERT);
//
//
//            StructureStart structureStart = structure.createStructureStart(
//                    accessor.getRegistryManager(),
//                    chunkManager.getChunkGenerator(),
//                    chunkManager.getChunkGenerator().getBiomeSource(),
//                    chunkManager.getNoiseConfig(),
//                    world.toServerWorld().getStructureTemplateManager(),
//                    world.getSeed(),
//                    new ChunkPos(new BlockPos(0,100,0)),
//                    0,
//                    world,
//                    registryEntry -> true
//            );
//
//            BlockBox blockBox = structureStart.getBoundingBox();
//            ChunkPos chunkPos1 = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMinX()), ChunkSectionPos.getSectionCoord(blockBox.getMinZ()));
//            ChunkPos chunkPos2 = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMaxX()), ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()));
//
//
//            try {
//                throwOnUnloadedPos(world.toServerWorld(), chunkPos1, chunkPos2);
//
//                ChunkPos.stream(chunkPos1, chunkPos2)
//                        .forEach(
//                                chunkPosx -> structureStart.place(
//                                        world,
//                                        accessor,
//                                        chunkManager.getChunkGenerator(),
//                                        world.getRandom(),
//                                        new BlockBox(chunkPosx.getStartX(), world.getBottomY(), chunkPosx.getStartZ(), chunkPosx.getEndX(), world.getTopY(), chunkPosx.getEndZ()),
//                                        chunkPosx
//                                )
//                        );
//
//                System.out.println("GENED");
//            } catch (CommandSyntaxException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk) {


    }


    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 0;
    }

    @Override
    public void setStructureStarts(DynamicRegistryManager dynamicRegistryManager, StructurePlacementCalculator structurePlacementCalculator, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager) {
        super.setStructureStarts(dynamicRegistryManager, structurePlacementCalculator, structureAccessor, chunk, structureTemplateManager);


    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {

        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView, NoiseConfig noiseConfig) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> list, NoiseConfig noiseConfig, BlockPos blockPos) {
    }
}
