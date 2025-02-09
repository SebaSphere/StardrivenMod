package dev.sebastianb.stardriven.command.ship.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.client.render.dimension.SpaceSkyRenderer;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import dev.sebastianb.stardriven.command.ICommand;
import dev.sebastianb.stardriven.dimension.StardrivenDimensions;
import dev.sebastianb.stardriven.dimension.generator.SpaceChunkGenerator;
import dev.sebastianb.stardriven.util.ship.ShipCreationUtils;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.nio.file.Path;
import java.util.OptionalLong;
import java.util.UUID;

import com.mojang.brigadier.arguments.StringArgumentType;

public class CreateShipCommand implements ICommand {
    @Override
    public String commandName() {
        return "create";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return LiteralArgumentBuilder.<ServerCommandSource>literal(commandName())
                .requires(source -> source.hasPermissionLevel(2))
                .executes(this::execute)
                .then(CommandManager.argument("shipName", StringArgumentType.string())
                        .executes(this::execute)
                        .then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
                                .then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(this::execute))))
                        .suggests((context, builder) -> {
                            String defaultName = String.format("\"%s's Ship\"", context.getSource().getName());
                            return builder.suggest(defaultName).buildFuture();
                        }));
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String shipName;
        try {
            shipName = StringArgumentType.getString(context, "shipName");
        } catch (Exception e) {
            shipName = source.getName() + "'s Ship";
        }

        // FIXME: surely there's a way to make this cleaner
        double x = 0, y = 0, z = 0;
        try {
            x = DoubleArgumentType.getDouble(context, "x");
            y = DoubleArgumentType.getDouble(context, "y");
            z = DoubleArgumentType.getDouble(context, "z");
        } catch (Exception e) {

        }



        RegistryEntryLookup<Biome> biomeRegistry
                = context.getSource().getServer().getRegistryManager().getWrapperOrThrow(RegistryKeys.BIOME);

        DynamicDimensionRegistry dynamicDimensionRegistry = DynamicDimensionRegistry.from(source.getServer());

        UUID shipUUID = UUID.randomUUID();

        ServerWorld shipWorld = ShipCreationUtils.createOrLoadShipWorld(biomeRegistry, dynamicDimensionRegistry, shipUUID);


        Path path = shipWorld.getServer().getSavePath(WorldSavePath.ROOT).resolve("dimensions/stardriven/interstellar-ship_" + shipUUID);

        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();

        dimensionalShipManager.init(path);


        dimensionalShipManager.createDimensionalShip(shipName, shipUUID,null, new DimensionalStarPosition(x, y, z));

        if (source.getPlayer() != null) {
            // send player to world with platform beneath
            source.getPlayer().teleport(shipWorld, 0.5, 100, 0.5, 0, 0);
            // create platform underneath player
            shipWorld.setBlockState(source.getPlayer().getBlockPos().down(), Blocks.GLOWSTONE.getDefaultState());
        }

        return 1;
    }

}
