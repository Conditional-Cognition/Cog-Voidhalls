package com.cogworks.voidhalls.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LayerZeroWallBlock extends Block {

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
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}