package dev.sebastianb.stardriven.data.model;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Optional;

public class StardrivenModelProvider extends FabricModelProvider {

    public StardrivenModelProvider(FabricDataOutput output) {
        super(output);

    }


    static class DirectionRotation {
        private final VariantSettings.Rotation rotationX;
        private final VariantSettings.Rotation rotationY;

        public DirectionRotation(VariantSettings.Rotation rotationX, VariantSettings.Rotation rotationY) {
            this.rotationX = rotationX;
            this.rotationY = rotationY;
        }

        public VariantSettings.Rotation getRotationX() {
            return rotationX;
        }

        public VariantSettings.Rotation getRotationY() {
            return rotationY;
        }
    }


    private VariantSettings.Rotation displayRotationToVariantRotation(DisplayBlock.DisplayRotation rotation) {
        switch (rotation) {
            case R0: return VariantSettings.Rotation.R0;
            case R90: return VariantSettings.Rotation.R90;
            case R180: return VariantSettings.Rotation.R180;
            case R270: return VariantSettings.Rotation.R270;
        }
        System.out.println("ERROR: Invalid display rotation: " + rotation);
        return VariantSettings.Rotation.R0;
    }

    private VariantSettings.Rotation addDisplayAndVariantRotation(VariantSettings.Rotation dRotation, VariantSettings.Rotation vRotation) {
        return VariantSettings.Rotation.values()[(dRotation.ordinal() + vRotation.ordinal()) % 4];
    }

    private VariantSettings.Rotation subtractRotations(VariantSettings.Rotation dRotation, VariantSettings.Rotation vRotation) {
        return VariantSettings.Rotation.values()[(dRotation.ordinal() - vRotation.ordinal() + 4) % 4];
    }

    private VariantSettings.Rotation multiplyRotations(VariantSettings.Rotation dRotation, VariantSettings.Rotation vRotation) {
        return VariantSettings.Rotation.values()[(dRotation.ordinal() * vRotation.ordinal()) % 4];
    }

    private VariantSettings.Rotation oppositeRotation(VariantSettings.Rotation rotation) {
        // Assuming VariantSettings.Rotation has 4 values (0, 1, 2, 3)
        switch (rotation) {
            case R0: return VariantSettings.Rotation.R180;
            case R90: return VariantSettings.Rotation.R270;
            case R180: return VariantSettings.Rotation.R0;
            case R270: return VariantSettings.Rotation.R90;
            default:
                throw new IllegalArgumentException("Unknown rotation value: " + rotation);
        }
    }


    public DirectionRotation rotationFromDirection(Direction direction, DisplayBlock.DisplayRotation displayRotation) {

        switch (direction) {
            case UP: {
                return new DirectionRotation(
                        VariantSettings.Rotation.R0,
                        addDisplayAndVariantRotation(displayRotationToVariantRotation(displayRotation), VariantSettings.Rotation.R0)
                );
            }
            case DOWN: {
                return new DirectionRotation(
                        VariantSettings.Rotation.R180,
                        addDisplayAndVariantRotation(displayRotationToVariantRotation(displayRotation), VariantSettings.Rotation.R0));
            }
            case EAST: {
                return new DirectionRotation(
                        VariantSettings.Rotation.R90,
                        VariantSettings.Rotation.R90
                );
            }
            case WEST: {
                return new DirectionRotation(VariantSettings.Rotation.R270, VariantSettings.Rotation.R90);
            }
            case NORTH: {
                return new DirectionRotation(VariantSettings.Rotation.R90, VariantSettings.Rotation.R0);
            }
            case SOUTH: {
                return new DirectionRotation(VariantSettings.Rotation.R90, VariantSettings.Rotation.R180);
            }

        }

        return new DirectionRotation(VariantSettings.Rotation.R270, VariantSettings.Rotation.R90);

    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {

        Block displayBlock = StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock();

        var centerModel = ModelIds.getBlockSubModelId(displayBlock, "");
        var cornerModel = ModelIds.getBlockSubModelId(displayBlock, "_corner");
        var sideModel = ModelIds.getBlockSubModelId(displayBlock, "_side_piece");
        var threeCornerModel = ModelIds.getBlockSubModelId(displayBlock, "_three_corner");
        var emptyModel = ModelIds.getBlockSubModelId(displayBlock, "_empty");
        var twoEdgePiece = ModelIds.getBlockSubModelId(displayBlock, "_two_side_piece");

        for (var block : StardrivenBlocks.DisplayBlocks.values()) {

            // I wish minecraft made it easier to work with this
            BlockStateVariantMap facingSupplier = BlockStateVariantMap.create(Properties.FACING)
                    .register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.X, VariantSettings.Rotation.R0))
                    .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.X, VariantSettings.Rotation.R180))
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R0)
                            .put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                            .put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                            .put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, centerModel)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                            .put(VariantSettings.X, VariantSettings.Rotation.R90));

            HashSet<BlockStateVariantMap> suppliers = new HashSet<>();

            VariantsBlockStateSupplier variantSupplier = VariantsBlockStateSupplier.create(block.asBlock());

            var displayFacing = BlockStateVariantMap.create(Properties.FACING, DisplayBlock.DISPLAY_PIECE, DisplayBlock.DISPLAY_ROTATION);

            for (Direction direction : Direction.values()) {
                for (DisplayBlock.DisplayPieceType displayPiece : DisplayBlock.DisplayPieceType.values()) {
                    for (DisplayBlock.DisplayRotation displayRotation: DisplayBlock.DisplayRotation.values()) {
                        DirectionRotation rotation = rotationFromDirection(direction, displayRotation);

                        VariantSettings.Rotation rotationX = rotation.getRotationX();
                        VariantSettings.Rotation rotationY = rotation.getRotationY();

                        switch (displayPiece) {
                            case EDGE -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, sideModel)
                            );
                            case CORNER -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, cornerModel)
                            );
                            case SINGLE -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, centerModel)
                            );
                            case THREE_EDGE -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, threeCornerModel)
                            );
                            case TWO_SIDE -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, twoEdgePiece));
                            case EMPTY -> suppliers.add(createDisplayFacingBlockstate(
                                    displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, emptyModel));

                        }
                    }
                }
            }


            for (var supplier : suppliers) {
                variantSupplier.coordinate(supplier);
            }

            // variantSupplier.coordinate(facingSupplier);

            generator.blockStateCollector.accept(variantSupplier);


        }

    }

    private BlockStateVariantMap createDisplayFacingBlockstate(
            BlockStateVariantMap.TripleProperty<Direction, DisplayBlock.DisplayPieceType, DisplayBlock.DisplayRotation> property,
            Direction direction, DisplayBlock.DisplayPieceType displayPiece, DisplayBlock.DisplayRotation displayRotation,
            VariantSettings.Rotation xRotation, VariantSettings.Rotation yRotation,
            Identifier model
    ) {


        return property
                .register(direction, displayPiece, displayRotation, BlockStateVariant.create().put(VariantSettings.MODEL, model)
                        .put(VariantSettings.X, xRotation)
                        .put(VariantSettings.Y, yRotation)
                );

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
