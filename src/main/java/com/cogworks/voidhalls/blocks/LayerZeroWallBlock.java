package com.cogworks.voidhalls.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LayerZeroWallBlock extends Block {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;

    public static final BooleanProperty NORTH_INNER = BooleanProperty.create("north_inner");
    public static final BooleanProperty SOUTH_INNER = BooleanProperty.create("south_inner");
    public static final BooleanProperty EAST_INNER  = BooleanProperty.create("east_inner");
    public static final BooleanProperty WEST_INNER  = BooleanProperty.create("west_inner");
    public static final BooleanProperty UP_INNER    = BooleanProperty.create("up_inner");
    public static final BooleanProperty DOWN_INNER  = BooleanProperty.create("down_inner");

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0,  0,  0,  16, 2,  16),
            Block.box(0,  14, 0,  16, 16, 16),
            Block.box(0,  0,  0,  2,  16, 16),
            Block.box(14, 0,  0,  16, 16, 16),
            Block.box(0,  0,  0,  16, 16, 2 ),
            Block.box(0,  0,  14, 16, 16, 16)
    );

    public LayerZeroWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, true).setValue(SOUTH, true)
                .setValue(EAST,  true).setValue(WEST,  true)
                .setValue(UP,    true).setValue(DOWN,  true)
                .setValue(NORTH_INNER, true).setValue(SOUTH_INNER, true)
                .setValue(EAST_INNER,  true).setValue(WEST_INNER,  true)
                .setValue(UP_INNER,    true).setValue(DOWN_INNER,  true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN,
                NORTH_INNER, SOUTH_INNER, EAST_INNER, WEST_INNER, UP_INNER, DOWN_INNER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        boolean adjNorth = level.getBlockState(pos.relative(Direction.NORTH)).is(this);
        boolean adjSouth = level.getBlockState(pos.relative(Direction.SOUTH)).is(this);
        boolean adjEast  = level.getBlockState(pos.relative(Direction.EAST)).is(this);
        boolean adjWest  = level.getBlockState(pos.relative(Direction.WEST)).is(this);
        boolean adjUp    = level.getBlockState(pos.relative(Direction.UP)).is(this);
        boolean adjDown  = level.getBlockState(pos.relative(Direction.DOWN)).is(this);
        return this.defaultBlockState()
                .setValue(NORTH, !adjNorth).setValue(SOUTH, !adjSouth)
                .setValue(EAST,  !adjEast) .setValue(WEST,  !adjWest)
                .setValue(UP,    !adjUp)   .setValue(DOWN,  !adjDown)
                .setValue(NORTH_INNER, !adjNorth).setValue(SOUTH_INNER, !adjSouth)
                .setValue(EAST_INNER,  !adjEast) .setValue(WEST_INNER,  !adjWest)
                .setValue(UP_INNER,    !adjUp)   .setValue(DOWN_INNER,  !adjDown);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, Direction dir, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        BooleanProperty outerProp = switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
        BooleanProperty innerProp = switch (dir) {
            case NORTH -> NORTH_INNER;
            case SOUTH -> SOUTH_INNER;
            case EAST  -> EAST_INNER;
            case WEST  -> WEST_INNER;
            case UP    -> UP_INNER;
            case DOWN  -> DOWN_INNER;
        };

        boolean isNeighborSame = neighborState.is(this);
        if (isNeighborSame) {
            state = state.setValue(outerProp, false);
        }
        return state.setValue(innerProp, !isNeighborSame);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return SHAPE;
    }
}