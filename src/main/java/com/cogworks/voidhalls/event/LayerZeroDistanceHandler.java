package com.cogworks.voidhalls.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "voidhalls", bus = EventBusSubscriber.Bus.GAME)
public class LayerZeroDistanceHandler {

    private static final ResourceLocation LAYER_ZERO_ID =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_0");
    private static final int MAX_DISTANCE = 5;

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel
                && serverLevel.dimension().location().equals(LAYER_ZERO_ID)) {
            serverLevel.getChunkSource().setViewDistance(MAX_DISTANCE);
            serverLevel.getChunkSource().setSimulationDistance(MAX_DISTANCE);
        }
    }

    // Safety net: re-clamp whenever a player actually enters the dimension,
    // in case something upstream re-synced the global distance for all levels.
    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(LAYER_ZERO_ID)
                && event.getEntity().level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().setViewDistance(MAX_DISTANCE);
            serverLevel.getChunkSource().setSimulationDistance(MAX_DISTANCE);
        }
    }
}