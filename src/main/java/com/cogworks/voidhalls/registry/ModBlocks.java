package com.cogworks.voidhalls.registry;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("voidhalls");

    private static DeferredBlock<ModTableBlock> registerCopy(String name, Block ofBlock) {
        return BLOCKS.register(
                name,
                () -> new ModTableBlock(
                        BlockBehaviour.Properties.ofFullCopy(ofBlock)
                )
        );
    }

    public static final DeferredBlock<ModTableBlock> OAK_TABLE =
            registerCopy("test_block", Blocks.OAK_PLANKS);

    public static final DeferredHolder<Block, LiquidBlock> TEST_FLUID_BLOCK =
            BLOCKS.register("test_fluid_block", () -> new LiquidBlock(
                    com.cogworks.nonsense.registry.ModFluids.TEST_FLUID_STILL.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
            ));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}