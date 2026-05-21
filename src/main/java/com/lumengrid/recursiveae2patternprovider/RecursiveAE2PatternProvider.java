package com.lumengrid.recursiveae2patternprovider;

import com.lumengrid.recursiveae2patternprovider.recipe.RecipeSerializers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RecursiveAE2PatternProvider.MODID)
public class RecursiveAE2PatternProvider {
    public static final String MODID = "recursiveae2patternprovider";
    public static final Logger LOGGER = LogUtils.getLogger();
    @SuppressWarnings("removal")
    public RecursiveAE2PatternProvider() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register recipe serializers
        RecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
    }
}
