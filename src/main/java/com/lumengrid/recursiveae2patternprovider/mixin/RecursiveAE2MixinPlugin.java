package com.lumengrid.recursiveae2patternprovider.mixin;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RecursiveAE2MixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("TileAssemblerMatrixPatternMixin")) {
            boolean isExtendedAELoaded = ModList.get().isLoaded("extendedae");
            if (!isExtendedAELoaded) {
                System.out.println("[RecursiveAE2PatternProvider] ExtendedAE not found, skipping TileAssemblerMatrixPatternMixin");
            }
            return isExtendedAELoaded;
        }
        if (mixinClassName.contains("AdvPatternProviderLogicMixin")) {
            boolean isAdvancedAELoaded = ModList.get().isLoaded("advanced_ae");
            if (!isAdvancedAELoaded) {
                System.out.println("[RecursiveAE2PatternProvider] AdvancedAE not found, skipping AdvPatternProviderLogicMixin");
            }
            return isAdvancedAELoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

