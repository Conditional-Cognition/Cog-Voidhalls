package com.cogworks.voidhalls.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
                    }).build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}