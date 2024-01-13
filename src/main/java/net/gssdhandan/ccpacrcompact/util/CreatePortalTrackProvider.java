package net.gssdhandan.ccpacrcompact.util;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Pair;
import net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPAUtil;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;


public class CreatePortalTrackProvider {
    public Pair<ServerLevel, BlockFace> provider(Pair<ServerLevel, BlockFace> inbound) {
        BlockFace blockFace = inbound.getSecond();
        BlockPos blockPos = new BlockPos(blockFace.getPos());
        blockPos = blockPos.relative(blockFace.getFace(), 1);
        Level worldIn = inbound.getFirst();
        Block portalBase = ExtendCPAUtil.getPortalBase(worldIn, blockPos);
        PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(portalBase);
        return standardPortalProvider(inbound,
                ExtendCPAUtil.createLevelKey(link.returnDimID),
                ExtendCPAUtil.createLevelKey(link.dimID), level -> {
            try {
                return (ITeleporter) Class.forName("net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPATeleporter")
                        .getDeclaredConstructor(ServerLevel.class)
                        .newInstance(level);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return level.getPortalForcer();
        });
    }

    public static Pair<ServerLevel, BlockFace> standardPortalProvider(Pair<ServerLevel, BlockFace> inbound,
                                                                      ResourceKey<Level> firstDimension, ResourceKey<Level> secondDimension,
                                                                      Function<ServerLevel, ITeleporter> customPortalForcer) {
        ServerLevel level = inbound.getFirst();
        ResourceKey<Level> resourcekey = level.dimension() == secondDimension ? firstDimension : secondDimension;
        MinecraftServer minecraftserver = level.getServer();
        ServerLevel otherLevel = minecraftserver.getLevel(resourcekey);

        if (otherLevel == null || !minecraftserver.isNetherEnabled())
            return null;

        BlockFace inboundTrack = inbound.getSecond();
        BlockPos portalPos = inboundTrack.getConnectedPos();
        BlockState portalState = level.getBlockState(portalPos);
        ITeleporter teleporter = customPortalForcer.apply(otherLevel);

        SuperGlueEntity probe = new SuperGlueEntity(level, new AABB(portalPos));
        probe.setYRot(inboundTrack.getFace()
                .toYRot());
        probe.setPortalEntrancePos();

        PortalInfo portalinfo = teleporter.getPortalInfo(probe, otherLevel, probe::findDimensionEntryPoint);
        if (portalinfo == null)
            return null;

        BlockPos otherPortalPos = BlockPos.containing(portalinfo.pos);
        BlockState otherPortalState = otherLevel.getBlockState(otherPortalPos);
        if (otherPortalState.getBlock() != portalState.getBlock())
            return null;

        Direction targetDirection = inboundTrack.getFace();
        if (targetDirection.getAxis() == otherPortalState.getValue(BlockStateProperties.AXIS))
            targetDirection = targetDirection.getClockWise();
        BlockPos otherPos = otherPortalPos.relative(targetDirection);
        return Pair.of(otherLevel, new BlockFace(otherPos, targetDirection.getOpposite()));
    }
}
