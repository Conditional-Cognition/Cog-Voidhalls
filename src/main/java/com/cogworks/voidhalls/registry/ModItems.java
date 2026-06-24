package com.cogworks.voidhalls.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("voidhalls");

    public static final DeferredItem<BlockItem> TEST_ITEM = ITEMS.registerSimpleBlockItem("test_item", ModBlocks.TEST_BLOCK);

    public static final DeferredItem<Item> TEST_FLUID_BUCKET =
            ITEMS.register("test_fluid_bucket",
                    () -> new BucketItem(
                            ModFluids.TEST_FLUID_STILL.get(),
                            new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                    ));

    public static final DeferredItem<BlockItem> LAYER_ZERO_WALL =
            ITEMS.registerSimpleBlockItem("layer_zero_wall", ModBlocks.LAYER_ZERO_WALL);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}