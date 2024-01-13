package net.gssdhandan.ccpacrcompact.cpaextend;

import net.gssdhandan.ccpacrcompact.CCpaCCConfig;
import net.gssdhandan.ccpacrcompact.util.CCPACRCPOI;
import net.gssdhandan.ccpacrcompact.util.ConfigAddPortal;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.portal.PortalPlacer;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.util.CustomTeleporter;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ExtendCPATeleporter extends CustomTeleporter implements ITeleporter {

	protected final ServerLevel level;
	public ExtendCPATeleporter(ServerLevel level) {
		this.level = level;
	}

	public Optional<BlockUtil.FoundRectangle> getExistingPortal(BlockPos pos, BlockState dimBlockState) {
		PoiManager poiManager = this.level.getPoiManager();
		poiManager.ensureLoadedAndValid(this.level, pos, 64);
		Optional<PoiRecord> optional = poiManager.getInSquare(
				(poiType) -> poiType.get() == CCPACRCPOI.CUSTOM_PORTAL.get(),
				pos, 64, PoiManager.Occupancy.ANY)
				.sorted(Comparator.<PoiRecord>comparingDouble(
						(poi) -> poi.getPos().distSqr(pos)).thenComparingInt(
								(poi) -> poi.getPos().getY()))
				.filter((poi) -> this.level.getBlockState(poi.getPos())
						.hasProperty(BlockStateProperties.AXIS))
				.filter((poi) -> ExtendCPAUtil.getPortalBase(this.level, poi.getPos()).defaultBlockState() == dimBlockState)
				.findFirst();
		return optional.map((poi) -> {
			BlockPos blockpos = poi.getPos();
			this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
			BlockState blockstate = this.level.getBlockState(blockpos);
			return BlockUtil.getLargestRectangleAround(blockpos, blockstate.getValue(BlockStateProperties.AXIS),
					21, Direction.Axis.Y, 21, (posIn) ->
					this.level.getBlockState(posIn) == blockstate);
		});
	}

	public Optional<BlockUtil.FoundRectangle> makePortal(BlockPos pos, Direction.Axis axis, BlockState dimBlockState) {
		Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		double d0 = -1.0D;
		BlockPos blockpos = null;
		double d1 = -1.0D;
		BlockPos blockpos1 = null;
		WorldBorder worldborder = this.level.getWorldBorder();
		int dimensionLogicalHeight = this.level.getHeight() - 1;
		BlockPos.MutableBlockPos mutablePos = pos.mutable();

		for(BlockPos.MutableBlockPos blockpos$mutable1 : BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {
			int j = Math.min(dimensionLogicalHeight, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockpos$mutable1.getX(), blockpos$mutable1.getZ()));
			if (worldborder.isWithinBounds(blockpos$mutable1) && worldborder.isWithinBounds(blockpos$mutable1.move(direction, 1))) {
				blockpos$mutable1.move(direction.getOpposite(), 1);

				for(int l = j; l >= 0; --l) {
					blockpos$mutable1.setY(l);
					if (this.level.isEmptyBlock(blockpos$mutable1)) {
						int i1;
						for(i1 = l; l > 0 && this.level.isEmptyBlock(blockpos$mutable1.move(Direction.DOWN)); --l) {
						}

						if (l + 4 <= dimensionLogicalHeight) {
							int j1 = i1 - l;
							if (j1 <= 0 || j1 >= 3) {
								blockpos$mutable1.setY(l);
								if (this.checkRegionForPlacement(blockpos$mutable1, mutablePos, direction, 0)) {
									double d2 = pos.distSqr(blockpos$mutable1);
									if (this.checkRegionForPlacement(blockpos$mutable1, mutablePos, direction, -1) && this.checkRegionForPlacement(blockpos$mutable1, mutablePos, direction, 1) && (d0 == -1.0D || d0 > d2)) {
										d0 = d2;
										blockpos = blockpos$mutable1.immutable();
									}

									if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
										d1 = d2;
										blockpos1 = blockpos$mutable1.immutable();
									}
								}
							}
						}
					}
				}
			}
		}

		if (d0 == -1.0D && d1 != -1.0D) {
			blockpos = blockpos1;
			d0 = d1;
		}

		if (d0 == -1.0D) {
			blockpos = (new BlockPos(pos.getX(), Mth.clamp(pos.getY(), 70, this.level.getHeight() - 10), pos.getZ())).immutable();
			Direction direction1 = direction.getClockWise();
			if (!worldborder.isWithinBounds(blockpos)) {
				return Optional.empty();
			}

			for(int l1 = -1; l1 < 2; ++l1) {
				for(int k2 = 0; k2 < 2; ++k2) {
					for(int i3 = -1; i3 < 3; ++i3) {
						BlockState blockstate1 = i3 < 0 ? dimBlockState : Blocks.AIR.defaultBlockState();
						mutablePos.setWithOffset(blockpos,
								k2 * direction.getStepX() + l1 * direction1.getStepX(), i3,
								k2 * direction.getStepZ() + l1 * direction1.getStepZ());
						this.level.setBlockAndUpdate(mutablePos, blockstate1);
					}
				}
			}
		}

		for(int k1 = -1; k1 < 3; ++k1) {
			for(int i2 = -1; i2 < 4; ++i2) {
				if (k1 == -1 || k1 == 2 || i2 == -1 || i2 == 3) {
					mutablePos.setWithOffset(blockpos, k1 * direction.getStepX(), i2, k1 * direction.getStepZ());
					this.level.setBlock(mutablePos, dimBlockState, 3);
				}
			}
		}

		BlockState drPortal = CustomPortalsMod.customPortalBlock.get()
				.defaultBlockState().setValue(CustomPortalBlock.AXIS, axis);

		for(int j2 = 0; j2 < 2; ++j2) {
			for(int l2 = 0; l2 < 3; ++l2) {
				mutablePos.setWithOffset(blockpos, j2 * direction.getStepX(), l2, j2 * direction.getStepZ());
				this.level.setBlock(mutablePos, drPortal, 18);
			}
		}

		return Optional.of(new BlockUtil.FoundRectangle(blockpos.immutable(), 2, 3));
	}

	private boolean checkRegionForPlacement(BlockPos originalPos, BlockPos.MutableBlockPos offsetPos, Direction directionIn, int offsetScale) {
		Direction direction = directionIn.getClockWise();

		for(int i = -1; i < 3; ++i) {
			for(int j = -1; j < 4; ++j) {
				offsetPos.setWithOffset(originalPos, directionIn.getStepX() * i + direction.getStepX() * offsetScale, j, directionIn.getStepZ() * i + direction.getStepZ() * offsetScale);
				if (j < 0 && !this.level.getBlockState(offsetPos).isSolid()) {
					return false;
				}

				if (j >= 0 && !this.level.isEmptyBlock(offsetPos)) {
					return false;
				}
			}
		}

		return true;
	}

	@Nullable
	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel level, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
		BlockPos blockPos = entity.portalEntrancePos.immutable();
		Block portalBase = ExtendCPAUtil.getPortalBase(entity.level(), blockPos);
		PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(portalBase);
		List<ResourceKey<Level>> blockDimList = List.of(ExtendCPAUtil.createLevelKey(link.returnDimID),
				ExtendCPAUtil.createLevelKey(link.dimID));
		ResourceKey<Level> entityLevel = entity.level().dimension();
		double coordinateDifference = ConfigAddPortal.portalLinkCoordinateDiffMap.getOrDefault(link, 1.0);
		if (coordinateDifference > 0.0000001){
			coordinateDifference = entityLevel ==
					blockDimList.get(0) ? coordinateDifference : 1.0 / coordinateDifference;
		}else {
			coordinateDifference = entityLevel ==
					blockDimList.get(0) ? 0.0 : 1.0;
		}
		WorldBorder border = level.getWorldBorder();
		BlockPos blockpos = border.clampToBounds(entity.getX() * coordinateDifference,
				entity.getY(), entity.getZ() * coordinateDifference);
		BlockState dimBlockState = portalBase.defaultBlockState();
		if (CCpaCCConfig.vanilla_portal || CCpaCCConfig.vanilla_move_entity){
			return customTPTarget(level, entity, blockpos, portalBase, link.getFrameTester());
		}
		return this.getOrMakePortal(entity, blockpos, dimBlockState).map((result) -> {
			BlockState blockstate = entity.level().getBlockState(entity.portalEntrancePos);
			Direction.Axis axis;
			Vec3 vector3d;
			if (blockstate.hasProperty(BlockStateProperties.AXIS)) {
				axis = blockstate.getValue(BlockStateProperties.AXIS);
				BlockUtil.FoundRectangle rectangle =
						BlockUtil.getLargestRectangleAround(entity.portalEntrancePos,
								axis, 21, Direction.Axis.Y, 21,
								(pos) -> entity.level().getBlockState(pos) == blockstate);
				vector3d = PortalShape.getRelativePosition(rectangle, axis, entity.position(), entity.getDimensions(entity.getPose()));
			} else {
				axis = Direction.Axis.X;
				vector3d = new Vec3(0.5D, 0.0D, 0.0D);
			}
			return PortalShape.createPortalInfo(level, result, axis, vector3d,
					entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
		}).orElse(null);
	}

	protected Optional<BlockUtil.FoundRectangle> getOrMakePortal(Entity entity, BlockPos pos, BlockState dimBlockState) {
		Optional<BlockUtil.FoundRectangle> existingPortal = this.getExistingPortal(pos, dimBlockState);
		if(existingPortal.isPresent()) {
			return existingPortal;
		}
		else {
			Direction.Axis portalAxis =
					this.level.getBlockState(entity.portalEntrancePos)
							.getOptionalValue(CustomPortalBlock.AXIS).orElse(Direction.Axis.X);
			return this.makePortal(pos, portalAxis, dimBlockState);
		}
	}

	public static PortalInfo createDestinationPortal(ServerLevel destination,
													 Entity entity, Direction.Axis axis,
													 BlockUtil.FoundRectangle portalFramePos,
													 BlockState frameBlock) {
		PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(frameBlock.getBlock());
		List<ResourceKey<Level>> blockDimList = List.of(ExtendCPAUtil.createLevelKey(link.returnDimID),
				ExtendCPAUtil.createLevelKey(link.dimID));
		ResourceKey<Level> entityLevel = entity.level().dimension();
		double coordinateDifference = ConfigAddPortal.portalLinkCoordinateDiffMap.getOrDefault(link, 1.0);
		if (coordinateDifference > 0.0000001){
			coordinateDifference = entityLevel ==
					blockDimList.get(0) ? coordinateDifference : 1.0 / coordinateDifference;
		}else {
			coordinateDifference = entityLevel ==
					blockDimList.get(0) ? 0.0 : 1.0;
		}
		WorldBorder worldBorder = destination.getWorldBorder();
		double xMin = Math.max(-2.9999872E7, worldBorder.getMinX() + 16.0);
		double zMin = Math.max(-2.9999872E7, worldBorder.getMinZ() + 16.0);
		double xMax = Math.min(2.9999872E7, worldBorder.getMaxX() - 16.0);
		double zMax = Math.min(2.9999872E7, worldBorder.getMinZ() - 16.0);
		double scaleFactor = coordinateDifference;
		BlockPos blockPos3 = new BlockPos((int)Mth.clamp(entity.getX() * scaleFactor, xMin, xMax),
				(int)entity.getY(), (int)Mth.clamp(entity.getZ() * scaleFactor, zMin, zMax));
		Optional<BlockUtil.FoundRectangle> portal = PortalPlacer.createDestinationPortal(destination, blockPos3, frameBlock, axis);
		if (portal.isPresent()) {
			PortalFrameTester portalFrameTester = CustomPortalApiRegistry.getPortalLinkFromBase(
					frameBlock.getBlock()).getFrameTester().createInstanceOfPortalFrameTester();
			CustomPortalsMod.portalLinkingStorage.createLink(
					portalFramePos.minCorner, entity.level().dimension(),
					((BlockUtil.FoundRectangle)portal.get()).minCorner, destination.dimension());
			return portalFrameTester.getTPTargetInPortal(
					(BlockUtil.FoundRectangle)portal.get(), axis, portalFrameTester.getEntityOffsetInPortal(portalFramePos, entity, axis), entity);
		} else {
			return idkWhereToPutYou(destination, entity, blockPos3);
		}
	}
}