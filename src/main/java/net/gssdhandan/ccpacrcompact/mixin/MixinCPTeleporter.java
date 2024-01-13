package net.gssdhandan.ccpacrcompact.mixin;

import net.gssdhandan.ccpacrcompact.CCPACRCompact;
import net.gssdhandan.ccpacrcompact.CCpaCCConfig;
import net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPATeleporter;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.kyrptonaught.customportalapi.util.CustomTeleporter;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.PortalInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomTeleporter.class)
public class MixinCPTeleporter {
    @Inject(method = "customTPTarget", at = @At(value = "INVOKE",
            target = "Lnet/kyrptonaught/customportalapi/util/CustomTeleporter;createDestinationPortal(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Direction$Axis;Lnet/minecraft/BlockUtil$FoundRectangle;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/portal/PortalInfo;"),
            cancellable = true, remap = false)
    private static void changeCoordinateDiff(ServerLevel destinationWorld,
                                             Entity entity, BlockPos enteredPortalPos,
                                             Block frameBlock,
                                             PortalFrameTester.PortalFrameTesterFactory portalFrameTesterFactory,
                                             CallbackInfoReturnable<PortalInfo> cir){
            if (!CCpaCCConfig.vanilla_move_entity){
                Direction.Axis portalAxis = CustomPortalHelper.getAxisFrom(entity.level().getBlockState(enteredPortalPos));
                BlockUtil.FoundRectangle fromPortalRectangle =
                        portalFrameTesterFactory.createInstanceOfPortalFrameTester()
                                .init(entity.level(), enteredPortalPos, portalAxis, new Block[]{frameBlock}).getRectangle();
                cir.setReturnValue(ExtendCPATeleporter
                        .createDestinationPortal(
                                destinationWorld, entity, portalAxis,
                                fromPortalRectangle, frameBlock.defaultBlockState()));
            }
    }
}
