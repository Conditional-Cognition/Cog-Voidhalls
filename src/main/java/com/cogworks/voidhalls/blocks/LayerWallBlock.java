package com.cogworks.voidhalls.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LayerWallBlock extends Block {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;

    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    static {
        VoxelShape[] faces = {
                Block.box(0,  0,  0,  16, 16, 1 ),
                Block.box(0,  0,  15, 16, 16, 16),
                Block.box(15, 0,  0,  16, 16, 16),
                Block.box(0,  0,  0,  1,  16, 16),
                Block.box(0,  15, 0,  16, 16, 16),
                Block.box(0,  0,  0,  16, 1,  16),
        };
        for (int i = 0; i < 64; i++) {
            VoxelShape shape = Shapes.empty();
            for (int bit = 0; bit < 6; bit++) {
                if ((i & (1 << bit)) != 0) {
                    shape = Shapes.or(shape, faces[bit]);
                }
            }
            SHAPES[i] = shape;
        }
    }

    private static int shapeIndex(BlockState state) {
        return (state.getValue(NORTH) ? 1  : 0)
                | (state.getValue(SOUTH) ? 2  : 0)
                | (state.getValue(EAST)  ? 4  : 0)
                | (state.getValue(WEST)  ? 8  : 0)
                | (state.getValue(UP)    ? 16 : 0)
                | (state.getValue(DOWN)  ? 32 : 0);
    }

    public LayerWallBlock(Properties properties) {
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        BlockState state = this.defaultBlockState();
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            boolean hidden = neighborState.isSolidRender(level, neighborPos) || neighborState.is(this);
            state = state.setValue(propertyFor(dir), !hidden);
        }
        boolean allFalse = !state.getValue(NORTH) && !state.getValue(SOUTH)
                && !state.getValue(EAST)  && !state.getValue(WEST)
                && !state.getValue(UP)    && !state.getValue(DOWN);
        if (allFalse) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    private static BooleanProperty propertyFor(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction dir, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        BooleanProperty prop = propertyFor(dir);

        if (neighborState.isSolidRender(level, neighborPos) || neighborState.is(this)) {
            state = state.setValue(prop, false);
        }
        boolean allFalse = !state.getValue(NORTH) && !state.getValue(SOUTH)
                && !state.getValue(EAST)  && !state.getValue(WEST)
                && !state.getValue(UP)    && !state.getValue(DOWN);
        if (allFalse) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return SHAPES[shapeIndex(state)];
    }
}