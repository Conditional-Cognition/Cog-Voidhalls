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
    // NEEDS TO BE REDONE

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;

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
                .setValue(UP,    true).setValue(DOWN,  true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    private boolean isOuter(LevelAccessor level, BlockPos pos, Direction dir) {
        return !level.getBlockState(pos.relative(dir)).is(this);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, isOuter(level, pos, Direction.NORTH))
                .setValue(SOUTH, isOuter(level, pos, Direction.SOUTH))
                .setValue(EAST,  isOuter(level, pos, Direction.EAST))
                .setValue(WEST,  isOuter(level, pos, Direction.WEST))
                .setValue(UP,    isOuter(level, pos, Direction.UP))
                .setValue(DOWN,  isOuter(level, pos, Direction.DOWN));
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, Direction dir, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        BooleanProperty prop = switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
        if (neighborState.is(this)) {
            return state.setValue(prop, false);
        }
        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return SHAPE;
    }
}