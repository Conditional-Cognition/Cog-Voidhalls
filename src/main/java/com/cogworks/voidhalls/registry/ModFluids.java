package com.cogworks.voidhalls.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, "nonsense");

    private static BaseFlowingFluid.Properties test_fluid_properties() {
        return new BaseFlowingFluid.Properties(
                com.cogworks.voidhalls.registry.ModFluidTypes.TEST_FLUID_TYPE,
                TEST_FLUID_STILL,
                TEST_FLUID_FLOWING
        )
                .bucket(ModItems.TEST_FLUID_BUCKET)
                .block(ModBlocks.TEST_FLUID_BLOCK);
    }

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> TEST_FLUID_STILL =
            FLUIDS.register("test_fluid",
                    () -> new BaseFlowingFluid.Source(test_fluid_properties()));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> TEST_FLUID_FLOWING =
            FLUIDS.register("test_fluid_flowing",
                    () -> new BaseFlowingFluid.Flowing(test_fluid_properties()));

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
