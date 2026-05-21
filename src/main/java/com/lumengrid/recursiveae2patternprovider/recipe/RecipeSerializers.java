package com.lumengrid.recursiveae2patternprovider.recipe;

import com.lumengrid.recursiveae2patternprovider.RecursiveAE2PatternProvider;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER, RecursiveAE2PatternProvider.MODID);

    public static final RegistryObject<RecipeSerializer<RecursivePatternRecipe>> RECURSIVE_PATTERN_SERIALIZER =
        RECIPE_SERIALIZERS.register("recursive_pattern", () -> new RecursivePatternRecipeSerializer());

    public static class RecursivePatternRecipeSerializer implements RecipeSerializer<RecursivePatternRecipe> {
        @Override
        public RecursivePatternRecipe fromJson(ResourceLocation id, JsonObject json) {
            // Stateless recipe - nothing to parse
            return new RecursivePatternRecipe();
        }

        @Override
        public RecursivePatternRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            // Stateless recipe - nothing serialized
            return new RecursivePatternRecipe();
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RecursivePatternRecipe recipe) {
            // Nothing to write
        }
    }
}
