package com.cogworks.voidhalls.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings({"unused"})
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "voidhalls");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOIDHALLS_TAB =
            CREATIVE_MODE_TABS.register("voidhalls_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.voidhalls"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.TEST_FLUID_BUCKET.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TEST_ITEM.get());
                        output.accept(ModItems.TEST_FLUID_BUCKET.get());

                        output.accept(ModItems.WOODEN_TABLESPOON.get());
                        output.accept(ModItems.STONE_TABLESPOON.get());
                        output.accept(ModItems.IRON_TABLESPOON.get());
                        output.accept(ModItems.GOLDEN_TABLESPOON.get());
                        output.accept(ModItems.DIAMOND_TABLESPOON.get());
                        output.accept(ModItems.NETHERITE_TABLESPOON.get());

                        output.accept(ModItems.OAK_TABLE.get());
                        output.accept(ModItems.SPRUCE_TABLE.get());
                        output.accept(ModItems.BIRCH_TABLE.get());
                        output.accept(ModItems.JUNGLE_TABLE.get());
                        output.accept(ModItems.ACACIA_TABLE.get());
                        output.accept(ModItems.DARK_OAK_TABLE.get());
                        output.accept(ModItems.MANGROVE_TABLE.get());
                        output.accept(ModItems.CHERRY_TABLE.get());
                        output.accept(ModItems.BAMBOO_TABLE.get());
                        output.accept(ModItems.CRIMSON_TABLE.get());
                        output.accept(ModItems.WARPED_TABLE.get());

                        output.accept(ModItems.ANDESITE_TABLE.get());
                        output.accept(ModItems.GRANITE_TABLE.get());
                        output.accept(ModItems.DIORITE_TABLE.get());
                        output.accept(ModItems.STONE_TABLE.get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LAYER_0_TAB =
            CREATIVE_MODE_TABS.register("voidhalls_layer_0_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.voidhalls.layer0"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.LAYER_ZERO_WALL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.LAYER_ZERO_WALLPAPER.get());
                        output.accept(ModItems.LAYER_ZERO_WALL.get());
                        output.accept(ModItems.LAYER_ZERO_WALL_STAIRS.get());
                        output.accept(ModItems.LAYER_ZERO_WALL_SLAB.get());
                        output.accept(ModItems.LAYER_ZERO_CEILING_TILE.get());
                        output.accept(ModItems.LAYER_ZERO_SCAFFOLD.get());
                    }).build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}