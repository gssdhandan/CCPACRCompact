package net.gssdhandan.ccpacrcompact.cpaextend;

import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ExtendCPAUtil {
    public static Block getPortalBase(Level world, BlockPos pos) {
        return CustomPortalHelper.getPortalBaseDefault(world, pos);
    }
    public static ResourceKey<Level> createLevelKey(ResourceLocation levelName){
        return ResourceKey.create(Registries.DIMENSION, levelName);
    }
}
