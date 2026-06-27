package com.cogworks.voidhalls.registry;

import com.cogworks.voidhalls.worldgen.Layer0ChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.cogworks.voidhalls.Voidhalls.MODID;

public class ModWorldgen {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MODID);
    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<Layer0ChunkGenerator>> LAYER_0_GENERATOR =
            CHUNK_GENERATORS.register("layer_0", () -> Layer0ChunkGenerator.CODEC);

    public static void register(IEventBus modEventBus) {
        CHUNK_GENERATORS.register(modEventBus);
    }
}
