package dev.sebastianb.stardriven.block.display;

import net.minecraft.block.*;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisplayBlock extends Block {

    public enum DisplayPieceType implements StringIdentifiable {
        CORNER("corner"),
        EDGE("edge"),
        SINGLE("single"),
        THREE_EDGE("three_edge"),
        EMPTY("empty"),
        TWO_SIDE("two_side");

        private final String name;

        DisplayPieceType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

    }

    public enum DisplayRotation implements StringIdentifiable {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        private final int rotation;

        DisplayRotation(int rotation) {
            this.rotation = rotation;
        }

        public int getRotation() {
            return rotation;
        }

        @Override
        public String asString() {
            return String.valueOf(this.rotation);
        }

    }


    public static final EnumProperty<DisplayPieceType> DISPLAY_PIECE = EnumProperty.of("display_piece", DisplayPieceType.class);
    public static final DirectionProperty FACING = Properties.FACING;

    public static final EnumProperty<DisplayRotation> DISPLAY_ROTATION =
            EnumProperty.of("display_rotation", DisplayRotation.class);


    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 2.001, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.001, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.001);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 14.001, 16.0, 16.0, 16.0);
    protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.001, 16.0);
    protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 14.001, 0.0, 16.0, 16.0, 16.0);


    public DisplayBlock(Settings settings) {
        super(settings);
    }



    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        switch (blockState.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case UP:
                return OPEN_BOTTOM_SHAPE;
            case DOWN:
                return OPEN_TOP_SHAPE;
        }
        return OPEN_BOTTOM_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                FACING,
                DISPLAY_PIECE,
                DISPLAY_ROTATION
        );
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        System.out.println(itemPlacementContext.getPlayerLookDirection().getOpposite());

        Direction facing = itemPlacementContext.getPlayerLookDirection().getOpposite();


        return this.getDefaultState()
                .with(FACING, itemPlacementContext.getPlayerLookDirection().getOpposite())
                .with(DISPLAY_PIECE, DisplayPieceType.SINGLE)
                .with(DISPLAY_ROTATION, DisplayRotation.R0);
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    private ArrayList<BlockPos> displayPositions = new ArrayList<>();

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        Direction direction = blockState.get(FACING);
        Direction[] directions;

        if (direction == Direction.UP || direction == Direction.DOWN) {
            directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            directions = new Direction[]{Direction.EAST, Direction.UP, Direction.WEST, Direction.DOWN};
        } else {
            directions = new Direction[]{Direction.NORTH, Direction.UP, Direction.SOUTH, Direction.DOWN};
        }

        checkForDisplay(world, blockPos, directions);

        System.out.println(displayPositions);


        displayPositions.clear();

        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
    }

    private void checkForDisplay(World world, BlockPos blockPos, Direction[] directions) {
        for (Direction dir : directions) {
            BlockPos placePos = blockPos.offset(dir);
            if (world.getBlockState(placePos).isOf(this)) {
                if (!displayPositions.contains(placePos)) {

                    displayPositions.add(placePos);

                    checkForDisplay(world, placePos, directions);
                }

            }

            if (world.getBlockState(placePos).isAir()) {
                world.setBlockState(placePos, Blocks.DIAMOND_BLOCK.getDefaultState());
            }
        }

    }

}
