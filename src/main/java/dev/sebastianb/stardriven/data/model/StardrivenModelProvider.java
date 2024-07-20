package dev.sebastianb.stardriven.data.model;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class StardrivenModelProvider extends FabricModelProvider {

    public StardrivenModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {

        for (var block : StardrivenBlocks.DisplayBlocks.values()) {

            var centerModel = ModelIds.getBlockSubModelId(block.asBlock(), "_corner");

            BlockStateVariantMap supplier = BlockStateVariantMap.create(Properties.FACING)
                    .register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                    .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel))
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel));



            generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block.asBlock())
                    .coordinate(
                            supplier
                    ));




        }

    }

    private static BlockStateVariant createVariant(Identifier modelId) {
        return BlockStateVariant.create().put(VariantSettings.MODEL, modelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        var block = StardrivenBlocks.DisplayBlocks.DISPLAY;

        generator.register(block.asItem(), blockItem(block.name().toLowerCase()));

    }

    private static Model blockItem(String string) {
        return new Model(Optional.of(new Identifier(Stardriven.MOD_ID, "block/" + string)), Optional.empty());
    }

}
