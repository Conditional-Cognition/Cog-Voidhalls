package com.cogworks.voidhalls;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("voidhalls");

    public static final DeferredItem<Item> TEST_ITEM = ITEMS.registerItem("test_item",
            props -> new BlockItem(ModBlocks.TEST_BLOCK.get(), props));

    public static final DeferredItem<Item> TEST_FLUID_BUCKET =
            ITEMS.register("test_fluid_bucket",
                    () -> new BucketItem(
                            ModFluids.TEST_FLUID_STILL.get(),
                            new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                    ));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}