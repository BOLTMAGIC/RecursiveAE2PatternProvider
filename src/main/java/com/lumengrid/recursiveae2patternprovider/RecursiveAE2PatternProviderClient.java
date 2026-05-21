package com.lumengrid.recursiveae2patternprovider;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(RecursiveAE2PatternProvider.MODID)
@Mod.EventBusSubscriber(modid = RecursiveAE2PatternProvider.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class RecursiveAE2PatternProviderClient {
    // Client-only class: keep a no-arg constructor for Forge (avoid ModContainer param)
    public RecursiveAE2PatternProviderClient() {
        // TODO: register a config GUI factory compatible with Forge; NeoForge-specific API removed during migration
        // Use ModLoadingContext or DistExecutor to register a client config screen.
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        RecursiveAE2PatternProvider.LOGGER.info("HELLO FROM CLIENT SETUP");
        RecursiveAE2PatternProvider.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
