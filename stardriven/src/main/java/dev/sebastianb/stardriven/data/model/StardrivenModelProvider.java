package dev.sebastianb.stardriven.data.model;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;

import java.util.*;

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
            case R0:
                return VariantSettings.Rotation.R0;
            case R90:
                return VariantSettings.Rotation.R90;
            case R180:
                return VariantSettings.Rotation.R180;
            case R270:
                return VariantSettings.Rotation.R270;
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
            case R0:
                return VariantSettings.Rotation.R180;
            case R90:
                return VariantSettings.Rotation.R270;
            case R180:
                return VariantSettings.Rotation.R0;
            case R270:
                return VariantSettings.Rotation.R90;
            default:
                throw new IllegalArgumentException("Unknown rotation value: " + rotation);
        }
    }


    public DirectionRotation rotationFromDirection(Direction direction) {

        switch (direction) {
            case UP: {
                return new DirectionRotation(VariantSettings.Rotation.R0, VariantSettings.Rotation.R0);
            }
            case DOWN: {
                return new DirectionRotation(VariantSettings.Rotation.R180, VariantSettings.Rotation.R0);
            }
            case EAST: {
                return new DirectionRotation(VariantSettings.Rotation.R90, VariantSettings.Rotation.R90);
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

    private static class MultipartStateEntry {
        private When condition;
        private BlockStateVariant state;

        public MultipartStateEntry(When condition, BlockStateVariant state) {
            this.condition = condition;
            this.state = state;
        }

        public When getCondition() {
            return condition;
        }

        public BlockStateVariant getState() {
            return state;
        }

        public void addToMultipart(MultipartBlockStateSupplier supplier) {
            supplier.with(condition, state);
        }
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {

        for (var block : StardrivenBlocks.DisplayBlocks.values()) {

            // var centerModel = ModelIds.getBlockSubModelId(block.asBlock(), "");
            // var cornerModel = ModelIds.getBlockSubModelId(block.asBlock(), "_corner");
            // var sideModel = ModelIds.getBlockSubModelId(block.asBlock(), "_side_piece");
            // var threeCornerModel = ModelIds.getBlockSubModelId(block.asBlock(), "_three_corner");

            var baseModel = ModelIds.getBlockSubModelId(block.asBlock(), "_base");
            var sideModel = ModelIds.getBlockSubModelId(block.asBlock(), "_side");

            HashSet<BlockStateVariantMap> suppliers = new HashSet<>();

            VariantsBlockStateSupplier variantSupplier = VariantsBlockStateSupplier.create(block.asBlock());

            // var displayFacing = BlockStateVariantMap.create(Properties.FACING, DisplayBlock.DISPLAY_PIECE, DisplayBlock.DISPLAY_ROTATION);

            // for (Direction direction : Direction.values()) {
            //     for (DisplayBlock.DisplayPieceType displayPiece : DisplayBlock.DisplayPieceType.values()) {
            //         for (DisplayBlock.DisplayRotation displayRotation : DisplayBlock.DisplayRotation.values()) {
            //             DirectionRotation rotation = rotationFromDirection(direction, displayRotation);

            //             VariantSettings.Rotation rotationX = rotation.getRotationX();
            //             VariantSettings.Rotation rotationY = rotation.getRotationY();

            //             switch (displayPiece) {
            //                 case EDGE -> suppliers.add(createDisplayFacingBlockstate(
            //                         displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, sideModel)
            //                 );
            //                 case CORNER -> suppliers.add(createDisplayFacingBlockstate(
            //                         displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, cornerModel)
            //                 );
            //                 case SINGLE -> suppliers.add(createDisplayFacingBlockstate(
            //                         displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, centerModel)
            //                 );
            //                 case THREE_EDGE -> suppliers.add(createDisplayFacingBlockstate(
            //                         displayFacing, direction, displayPiece, displayRotation, rotationX, rotationY, threeCornerModel)
            //                 );

            //             }
            //         }
            //     }
            // }

            List<MultipartStateEntry> states = new ArrayList<>();

            for (Direction direction : Direction.values()) {
                DirectionRotation directionRotation = rotationFromDirection(direction);

                states.add(new MultipartStateEntry(
                        When.create().set(DisplayBlock.FACING, direction),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, baseModel)
                                .put(VariantSettings.X, directionRotation.rotationX)
                                .put(VariantSettings.Y, directionRotation.rotationY)
                ));

                states.add(new MultipartStateEntry(When.allOf(
                        When.create().set(DisplayBlock.TOP_CONNECTED, Boolean.TRUE),
                        When.create().set(DisplayBlock.FACING, direction)
                        ),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, sideModel)
                                .put(VariantSettings.X, directionRotation.rotationX)
                                .put(VariantSettings.Y, directionRotation.rotationY)
                ));
            }

            MultipartBlockStateSupplier multipartSupplier = MultipartBlockStateSupplier.create(block.asBlock());

            for (MultipartStateEntry state : states) {
                state.addToMultipart(multipartSupplier);
            }

            generator.blockStateCollector.accept(multipartSupplier);
        }

    }

    private BlockStateVariantMap createDisplayFacingBlockstateWithConnections(BlockStateVariantMap.DoubleProperty<Direction, Boolean> property, Direction direction, Boolean topConnected, Identifier model) {
        return property.register(direction, topConnected, BlockStateVariant.create().put(VariantSettings.MODEL, model));
    }

    private BlockStateVariantMap createDisplayFacingBlockstate(BlockStateVariantMap.TripleProperty<Direction, DisplayBlock.DisplayPieceType, DisplayBlock.DisplayRotation> property, Direction direction, DisplayBlock.DisplayPieceType displayPiece, DisplayBlock.DisplayRotation displayRotation, VariantSettings.Rotation xRotation, VariantSettings.Rotation yRotation, Identifier model) {
        return property.register(direction, displayPiece, displayRotation, BlockStateVariant.create().put(VariantSettings.MODEL, model).put(VariantSettings.X, xRotation).put(VariantSettings.Y, yRotation));
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
