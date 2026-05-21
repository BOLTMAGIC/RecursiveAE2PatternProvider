package com.lumengrid.recursiveae2patternprovider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Utility for checking AE2 patterns and recursive flags
 */
public class PatternUtil {
    
    /**
     * Check if an item is any AE2 pattern (crafting, processing, smithing, etc.)
     */
    public static boolean isAE2Pattern(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        try {
            // Compare registry names to avoid direct dependency on AE2 API methods that differ between versions
            ResourceLocation id = stack.getItem().builtInRegistryHolder().key().location();
            if (id == null) return false;
            String ns = id.getNamespace();
            String path = id.getPath();
            // AE2 items are under the appliedenergistics2 namespace and contain 'pattern' in their path
            return "appliedenergistics2".equals(ns) && path.contains("pattern");
        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.debug("Error checking if item is AE2 pattern: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if a pattern has the recursive flag enabled
     */
    public static boolean isRecursive(ItemStack patternStack) {
        if (patternStack.isEmpty() || !isAE2Pattern(patternStack)) {
            return false;
        }
        
        try {
            CompoundTag tag = patternStack.getTag();
            if (tag != null) {
                return tag.getBoolean("recursive");
            }
        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.debug("Failed to read recursive flag: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create a recursive version of a pattern for JEI display purposes
     */
    public static ItemStack createRecursivePattern(ItemStack originalPattern) {
        try {
            ItemStack newPattern = originalPattern.copy();

            // Add recursive flag to NBT
            CompoundTag customData = newPattern.getOrCreateTag();
            customData.putBoolean("recursive", true);
            newPattern.setTag(customData);

            return newPattern;

        } catch (Exception e) {
            RecursiveAE2PatternProvider.LOGGER.error("Failed to create recursive pattern for JEI: {}", e.getMessage());
            return originalPattern.copy();
        }
    }

    /**
     * Compatibility helper: compare two ItemStacks for same item and equal NBT components
     */
    public static boolean isSameItemSameComponents(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        CompoundTag ta = a.getTag();
        CompoundTag tb = b.getTag();
        if (ta == null && tb == null) return true;
        if (ta == null || tb == null) return false;
        return ta.equals(tb);
    }
}
