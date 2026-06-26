package com.cogworks.voidhalls;

import com.cogworks.voidhalls.registry.ModFluidTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(value = Voidhalls.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Voidhalls.MODID, value = Dist.CLIENT)
public class VoidhallsClient {
    public VoidhallsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Voidhalls.LOGGER.info("HELLO FROM CLIENT SETUP");
        Voidhalls.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public int getTintColor() {
                return 0xDDEE00EE;
            }
        }, ModFluidTypes.TEST_FLUID_TYPE.get());
    }
    @SubscribeEvent
    public static void onRegisterRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath("voidhalls", "test_fluid_block"),
                RenderType.translucent(),
                RenderType.translucentMovingBlock()
        );
    }
}
