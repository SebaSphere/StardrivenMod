package dev.sebastianb.stardriven.mixin.ship;

import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import dev.sebastianb.stardriven.dimension.generator.SpaceChunkGenerator;
import dev.sebastianb.stardriven.util.ship.ShipCreationUtils;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import net.terradevelopment.terrautil.api_imp.file.NbtFileIOImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract File getRunDirectory();

    @Shadow @Final protected LevelStorage.Session session;

    @Shadow public abstract CommandManager getCommandManager();

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void start(CallbackInfo ci) {

    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z", shift = At.Shift.AFTER))
    private void init(CallbackInfo ci) {
        // TODO: load all dimensional ships
        Path stardrivenFolderPath = this.session.getDirectory(WorldSavePath.ROOT).resolve("./dimensions/stardriven").toAbsolutePath();
        try (Stream<Path> walk = Files.walk(stardrivenFolderPath)) {
            List<Path> folderPathsList = walk
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("interstellar-ship_"))
                    .filter(path -> {
                        String folderName = path.getFileName().toString();
                        String uuidPart = folderName.split("_")[1];
                        try {
                            UUID.fromString(uuidPart);
                            return true;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    })
                    .toList();
            folderPathsList.forEach(this::loadDimension);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    DynamicDimensionRegistry dynamicDimensionRegistry = DynamicDimensionRegistry.from((MinecraftServer) (Object) this);

    private void loadDimension(Path path) {
        RegistryEntryLookup<Biome> biomeRegistry
                = ((MinecraftServer) (Object) this).getRegistryManager().getWrapperOrThrow(RegistryKeys.BIOME);

        UUID shipUUID = UUID.fromString(path.getFileName().toString().split("_")[1]);

        ServerWorld world = ShipCreationUtils.createOrLoadShipWorld(biomeRegistry, dynamicDimensionRegistry, shipUUID);

        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();

        dimensionalShipManager.init(world, shipUUID);


    }


}