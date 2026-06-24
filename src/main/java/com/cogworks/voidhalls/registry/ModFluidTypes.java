package com.cogworks.nonsense.registry;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, "voidhalls");

    public static final DeferredHolder<FluidType, FluidType> TEST_FLUID_TYPE =
            FLUID_TYPES.register("test_fluid_type", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid.nonsense.test_fluid")
                            .density(1000)
                            .viscosity(1000)
                            .lightLevel(0)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            ));

    public static void register(iEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}