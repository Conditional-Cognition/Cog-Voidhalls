package com.cogworks.voidhalls.registry;

import com.cogworks.voidhalls.items.TablespoonItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("voidhalls");

    // --- TEST ITEMS ---
    public static final DeferredItem<BlockItem> TEST_ITEM = ITEMS.registerSimpleBlockItem("test_item", ModBlocks.TEST_BLOCK);

    public static final DeferredItem<Item> TEST_FLUID_BUCKET =
            ITEMS.register("test_fluid_bucket",
                    () -> new BucketItem(
                            ModFluids.TEST_FLUID_STILL.get(),
                            new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
                    ));

    // --- WOOD TABLES ---
    public static final DeferredItem<Item> OAK_TABLE = 
            ITEMS.registerItem("oak_table", props -> new BlockItem(ModBlocks.OAK_TABLE.get(), props));
    public static final DeferredItem<Item> SPRUCE_TABLE = 
            ITEMS.registerItem("spruce_table", props -> new BlockItem(ModBlocks.SPRUCE_TABLE.get(), props));
    public static final DeferredItem<Item> BIRCH_TABLE = 
            ITEMS.registerItem("birch_table", props -> new BlockItem(ModBlocks.BIRCH_TABLE.get(), props));
    public static final DeferredItem<Item> JUNGLE_TABLE = 
            ITEMS.registerItem("jungle_table", props -> new BlockItem(ModBlocks.JUNGLE_TABLE.get(), props));
    public static final DeferredItem<Item> ACACIA_TABLE = 
            ITEMS.registerItem("acacia_table", props -> new BlockItem(ModBlocks.ACACIA_TABLE.get(), props));
    public static final DeferredItem<Item> DARK_OAK_TABLE = 
            ITEMS.registerItem("dark_oak_table", props -> new BlockItem(ModBlocks.DARK_OAK_TABLE.get(), props));
    public static final DeferredItem<Item> MANGROVE_TABLE = 
            ITEMS.registerItem("mangrove_table", props -> new BlockItem(ModBlocks.MANGROVE_TABLE.get(), props));
    public static final DeferredItem<Item> CHERRY_TABLE = 
            ITEMS.registerItem("cherry_table", props -> new BlockItem(ModBlocks.CHERRY_TABLE.get(), props));
    public static final DeferredItem<Item> BAMBOO_TABLE = 
            ITEMS.registerItem("bamboo_table", props -> new BlockItem(ModBlocks.BAMBOO_TABLE.get(), props));
    public static final DeferredItem<Item> CRIMSON_TABLE = 
            ITEMS.registerItem("crimson_table", props -> new BlockItem(ModBlocks.CRIMSON_TABLE.get(), props));
    public static final DeferredItem<Item> WARPED_TABLE = 
            ITEMS.registerItem("warped_table", props -> new BlockItem(ModBlocks.WARPED_TABLE.get(), props));

    // --- METAL TABLES ---
    public static final DeferredItem<Item> ANDESITE_TABLE = 
            ITEMS.registerItem("andesite_table", props -> new BlockItem(ModBlocks.ANDESITE_TABLE.get(), props));
    public static final DeferredItem<Item> GRANITE_TABLE = 
            ITEMS.registerItem("granite_table", props -> new BlockItem(ModBlocks.GRANITE_TABLE.get(), props));
    public static final DeferredItem<Item> DIORITE_TABLE = 
            ITEMS.registerItem("diorite_table", props -> new BlockItem(ModBlocks.DIORITE_TABLE.get(), props));
    public static final DeferredItem<Item> STONE_TABLE = 
            ITEMS.registerItem("stone_table", props -> new BlockItem(ModBlocks.STONE_TABLE.get(), props));

    // --- TABLESPOONS ---
    public static final DeferredItem<Item> WOODEN_TABLESPOON = 
            ITEMS.registerItem("wooden_tablespoon", props -> new TablespoonItem(Tiers.WOOD, props));
    public static final DeferredItem<Item> STONE_TABLESPOON = 
            ITEMS.registerItem("stone_tablespoon", props -> new TablespoonItem(Tiers.STONE, props));
    public static final DeferredItem<Item> IRON_TABLESPOON = 
            ITEMS.registerItem("iron_tablespoon", props -> new TablespoonItem(Tiers.IRON, props));
    public static final DeferredItem<Item> GOLDEN_TABLESPOON = 
            ITEMS.registerItem("golden_tablespoon", props -> new TablespoonItem(Tiers.GOLD, props));
    public static final DeferredItem<Item> DIAMOND_TABLESPOON = 
            ITEMS.registerItem("diamond_tablespoon", props -> new TablespoonItem(Tiers.DIAMOND, props));
    public static final DeferredItem<Item> NETHERITE_TABLESPOON = 
            ITEMS.registerItem("netherite_tablespoon", props -> new TablespoonItem(Tiers.NETHERITE, props.fireResistant()));

    // --- LAYER 0 ASSETS ---
    public static final DeferredItem<BlockItem> LAYER_ZERO_WALLPAPER =
            ITEMS.registerSimpleBlockItem("layer_zero_wallpaper", ModBlocks.LAYER_ZERO_WALLPAPER);
    public static final DeferredItem<BlockItem> LAYER_ZERO_WALL =
            ITEMS.registerSimpleBlockItem("layer_zero_wall", ModBlocks.LAYER_ZERO_WALL);
    public static final DeferredItem<BlockItem> LAYER_ZERO_WALL_STAIRS =
            ITEMS.registerSimpleBlockItem("layer_zero_wall_stairs", ModBlocks.LAYER_ZERO_WALL_STAIRS);
    public static final DeferredItem<BlockItem> LAYER_ZERO_WALL_SLAB =
            ITEMS.registerSimpleBlockItem("layer_zero_wall_slab", ModBlocks.LAYER_ZERO_WALL_SLAB);
    public static final DeferredItem<BlockItem> LAYER_ZERO_CEILING_TILE =
            ITEMS.registerSimpleBlockItem("layer_zero_ceiling_tile", ModBlocks.LAYER_ZERO_CEILING_TILE);
    public static final DeferredItem<BlockItem> LAYER_ZERO_SCAFFOLD =
            ITEMS.registerSimpleBlockItem("layer_zero_scaffold", ModBlocks.LAYER_ZERO_SCAFFOLD);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}