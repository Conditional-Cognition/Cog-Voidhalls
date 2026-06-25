package com.cogworks.voidhalls.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.cogworks.voidhalls.blocks.*;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("voidhalls");

    public static final DeferredBlock<Block> TEST_BLOCK =
            BLOCKS.registerSimpleBlock("test_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final DeferredHolder<Block, LiquidBlock> TEST_FLUID_BLOCK =
            BLOCKS.register("test_fluid_block", () -> new LiquidBlock(
                    ModFluids.TEST_FLUID_STILL.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
            ));

    public static final DeferredBlock<LayerWallBlock> LAYER_ZERO_WALL =
            BLOCKS.register("layer_zero_wall", () -> new LayerWallBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
            ));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}