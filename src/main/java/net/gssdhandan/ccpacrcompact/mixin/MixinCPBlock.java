package net.gssdhandan.ccpacrcompact.mixin;

import net.gssdhandan.ccpacrcompact.CCPACRCompact;
import net.gssdhandan.ccpacrcompact.CCpaCCConfig;
import net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPATeleporter;
import net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPAUtil;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.interfaces.EntityInCustomPortal;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CustomPortalBlock.class)
public class MixinCPBlock {
    @Inject(method = "m_7892_", at = @At(value = "INVOKE",
            target = "Lnet/kyrptonaught/customportalapi/util/CustomTeleporter;TPToDim(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V"),
            cancellable = true, remap = false)
    private void replaceTeleport(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci){
        if (!CCpaCCConfig.vanilla_move_entity){
            if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
                if (entity.isOnPortalCooldown()) {
                    entity.setPortalCooldown();
                } else {
                    if (!entity.level().isClientSide && !pos.equals(entity.portalEntrancePos)) {
                        entity.portalEntrancePos = pos.immutable();
                    }
                    Level entityWorld = entity.level();
                    if (entityWorld != null) {
                        Block portalBase = ExtendCPAUtil.getPortalBase(world, pos);
                        MinecraftServer minecraftserver = entityWorld.getServer();
                        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(portalBase);
                        if (link == null) {
                            ci.cancel();
                            return;
                        }
                        List<ResourceKey<Level>> blockDimList = List.of(ExtendCPAUtil.createLevelKey(link.returnDimID),
                                ExtendCPAUtil.createLevelKey(link.dimID));
                        ResourceKey<Level> entityLevel = entity.level().dimension();
                        if (entityLevel != blockDimList.get(0)
                                && entityLevel != blockDimList.get(1)) {
                            ci.cancel();
                            return;
                        }
                        ResourceKey<Level> destination = entityLevel ==
                                blockDimList.get(0) ? blockDimList.get(1) : blockDimList.get(0);
                        if (minecraftserver != null) {
                            ServerLevel destinationWorld = minecraftserver.getLevel(destination);
                            if (destinationWorld != null && minecraftserver.isNetherEnabled() && !entity.isPassenger()) {
                                entity.level().getProfiler().push("custom_portal_block");
                                entity.setPortalCooldown();
                                entity.changeDimension(destinationWorld, new ExtendCPATeleporter(destinationWorld));
                                entity.level().getProfiler().pop();
                            }
                        }
                    }
                }
            }
            ci.cancel();
        }
    }

}
