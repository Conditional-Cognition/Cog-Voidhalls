package com.cogworks.voidhalls.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class LayerWallBlock extends Block {

    public static final BooleanProperty GROUNDED = BooleanProperty.create("grounded");

    public LayerWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(GROUNDED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GROUNDED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(GROUNDED, isGrounded(ctx.getLevel(), ctx.getClickedPos()));
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction dir, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (dir == Direction.DOWN) {
            return state.setValue(GROUNDED, isGrounded(level, pos));
        }
        return state;
    }

    private boolean isGrounded(BlockGetter level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return !below.isAir() && !below.is(this);
    }
}