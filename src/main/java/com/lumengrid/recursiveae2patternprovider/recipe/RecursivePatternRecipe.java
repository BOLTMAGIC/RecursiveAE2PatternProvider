package com.lumengrid.recursiveae2patternprovider.recipe;

import com.lumengrid.recursiveae2patternprovider.Config;
import com.lumengrid.recursiveae2patternprovider.PatternUtil;
import com.lumengrid.recursiveae2patternprovider.RecursiveAE2PatternProvider;
import appeng.core.definitions.AEItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Recipe for managing recursive patterns:
 * - Normal Pattern + Recipe Item → Recursive Pattern
 * - Recursive Pattern (alone) → Normal Pattern  
 * - Recursive Pattern + Recipe Item → No recipe (doesn't work)
 * Recipe item is configurable via config (default: iron ingot)
 * Works with ALL AE2 pattern types (crafting, processing, smithing, stonecutting, etc.)
 * Note: Recipes are also displayed in JEI for reference
 */
public class RecursivePatternRecipe implements CraftingRecipe {
    
    /**
     * Get the configured recipe item from config
     */
    private Item getConfiguredRecipeItem() {
        try {
            String itemName = Config.RECIPE_ITEM.get();
            ResourceLocation itemId = ResourceLocation.parse(itemName);
            return BuiltInRegistries.ITEM.get(itemId);
        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.warn("Invalid recipe item configured: '{}', falling back to iron ingot", Config.RECIPE_ITEM.get());
            return Items.IRON_INGOT;
        }
    }
    
    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        ItemStack pattern = ItemStack.EMPTY;
        ItemStack recipeItem = ItemStack.EMPTY;
        int itemCount = 0;
        Item configuredItem = getConfiguredRecipeItem();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                itemCount++;
                if (PatternUtil.isAE2Pattern(stack)) {
                    pattern = stack;
                    RecursiveAE2PatternProvider.LOGGER.debug("Found AE2 pattern in recipe: {}", 
                        stack.getItem().builtInRegistryHolder().key().location());
                } else if (stack.is(configuredItem)) {
                    recipeItem = stack;
                }
            }
        }

        // Accept two scenarios:
        // 1. NON-recursive Pattern + Recipe Item (2 items) - for making patterns recursive
        // 2. Recursive Pattern alone (1 item) - for removing recursive tag
        if (itemCount == 2 && !pattern.isEmpty() && !recipeItem.isEmpty() && !PatternUtil.isRecursive(pattern)) {
            return true; // NON-recursive Pattern + Recipe Item only
        } else if (itemCount == 1 && !pattern.isEmpty() && PatternUtil.isRecursive(pattern)) {
            return true; // Recursive pattern alone
        }

        return false;
    }
    
    @Override
    public ItemStack assemble(CraftingContainer inv, net.minecraft.core.RegistryAccess registries) {
        ItemStack pattern = ItemStack.EMPTY;
        boolean hasRecipeItem = false;
        int itemCount = 0;
        Item configuredItem = getConfiguredRecipeItem();

        // Find pattern and check for configured recipe item
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                itemCount++;
                if (PatternUtil.isAE2Pattern(stack)) {
                    pattern = stack;
                } else if (stack.is(configuredItem)) {
                    hasRecipeItem = true;
                }
            }
        }

        if (!pattern.isEmpty()) {
            if (itemCount == 2 && hasRecipeItem) {
                // Non-recursive Pattern + Recipe Item: Add recursive flag
                // (pattern is guaranteed to be non-recursive by matches() method)
                RecursiveAE2PatternProvider.LOGGER.debug("Adding recursive flag to: {}",
                    pattern.getItem().builtInRegistryHolder().key().location());
                return createRecursivePattern(pattern);
            } else if (itemCount == 1 && PatternUtil.isRecursive(pattern)) {
                // Recursive Pattern alone: Remove recursive flag
                RecursiveAE2PatternProvider.LOGGER.debug("Removing recursive flag from: {}",
                    pattern.getItem().builtInRegistryHolder().key().location());
                return removeRecursiveFlag(pattern);
            }
        }

        return ItemStack.EMPTY;
    }
    
    private ItemStack createRecursivePattern(ItemStack originalPattern) {
        try {
            ItemStack newPattern = originalPattern.copy();

            // Add recursive flag to NBT
            CompoundTag customData = newPattern.getOrCreateTag();
            customData.putBoolean("recursive", true);
            newPattern.setTag(customData);

            RecursiveAE2PatternProvider.LOGGER.debug("Created recursive pattern with NBT: {}", customData);
            return newPattern;

        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.error("Failed to create recursive pattern: {}", e.getMessage());
            return ItemStack.EMPTY;
        }
    }
    
    private ItemStack removeRecursiveFlag(ItemStack recursivePattern) {
        try {
            ItemStack newPattern = recursivePattern.copy();

            // Remove recursive flag from NBT
            CompoundTag customData = newPattern.getTag();
            if (customData != null) {
                customData.remove("recursive");

                // If custom data is now empty, remove the tag entirely
                if (customData.isEmpty()) {
                    newPattern.setTag(null);
                    RecursiveAE2PatternProvider.LOGGER.debug("Removed all custom data from pattern");
                } else {
                    newPattern.setTag(customData);
                    RecursiveAE2PatternProvider.LOGGER.debug("Removed recursive flag, remaining NBT: {}", customData);
                }
            }

            return newPattern;

        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.error("Failed to remove recursive flag: {}", e.getMessage());
            return ItemStack.EMPTY;
        }
    }
    
    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
        // Return a generic crafting pattern for recipe book display
        // The actual result depends on the input pattern type
        return AEItems.CRAFTING_PATTERN.stack();
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.RECURSIVE_PATTERN_SERIALIZER.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
    
    @Override
    public ResourceLocation getId() {
        //noinspection removal
        return new ResourceLocation(RecursiveAE2PatternProvider.MODID, "recursive_pattern");
    }

    @Override
    public net.minecraft.world.item.crafting.CraftingBookCategory category() {
        return net.minecraft.world.item.crafting.CraftingBookCategory.MISC;
    }
}
