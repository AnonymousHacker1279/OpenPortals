package tech.anonymoushacker1279.openportals.portal.frame;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.portal.PortalIgnitionSource;
import tech.anonymoushacker1279.openportals.portal.PortalLink;
import tech.anonymoushacker1279.openportals.portal.frame.api.PortalBuilder;
import tech.anonymoushacker1279.openportals.portal.frame.api.PortalTeleporter;
import tech.anonymoushacker1279.openportals.portal.frame.api.PortalValidator;
import tech.anonymoushacker1279.openportals.util.PortalConstants;

import java.util.HashSet;
import java.util.function.Predicate;

/**
 * Abstract base class for portal frame testing and manipulation.
 * <p>
 * Subclasses should implement the specific portal shape logic (vertical, horizontal, etc.).
 */
public abstract class PortalFrameTester implements PortalValidator, PortalBuilder, PortalTeleporter {

	@Nullable
	public BlockPos lowerCorner;
	protected HashSet<Block> VALID_FRAME = new HashSet<>();
	protected int foundPortalBlocks;
	protected LevelAccessor levelAccessor;

	public static boolean validStateInsidePortal(BlockState blockState, HashSet<Block> foundations) {
		PortalIgnitionSource ignitionSource = PortalIgnitionSource.FIRE;
		for (Block block : foundations) {
			PortalLink link = OpenPortals.getPortalManager().getPortalLinkFromBase(block);
			if (link != null) {
				ignitionSource = link.getIgnitionSource();
				break;
			}
		}

		if (blockState.isAir() || blockState.getBlock() instanceof CustomPortalBlock) {
			return true;
		}
		if (ignitionSource == PortalIgnitionSource.FIRE) {
			return blockState.is(BlockTags.FIRE);
		}
		if (ignitionSource.isWater()) {
			return blockState.getFluidState().is(FluidTags.WATER);
		}
		if (ignitionSource.isLava()) {
			return blockState.getFluidState().is(FluidTags.LAVA);
		}
		if (ignitionSource.sourceType == PortalIgnitionSource.SourceType.FLUID) {
			return BuiltInRegistries.FLUID.getKey(blockState.getFluidState().getType()) == ignitionSource.ignitionSourceID;
		}

		return false;
	}

	@Override
	public abstract PortalValidator init(LevelAccessor level, BlockPos blockPos, Axis axis, Block... foundations);

	/**
	 * Try to find a new, unlit portal frame at the given position.
	 *
	 * @return this validator if a valid unlit portal is found, null otherwise
	 */
	@Nullable
	public abstract PortalValidator getNewPortal(LevelAccessor level, BlockPos blockPos, Axis axis, Block... foundations);

	/**
	 * Try to find a portal frame matching the given predicate.
	 *
	 * @return this validator if a matching portal is found, null otherwise
	 */
	@Nullable
	public abstract PortalValidator getOrEmpty(LevelAccessor level, BlockPos blockPos,
	                                           Predicate<PortalFrameTester> predicate, Direction.Axis axis,
	                                           Block... foundations);

	@Override
	public abstract boolean isAlreadyLitPortalFrame();

	@Override
	public abstract boolean isValidFrame();

	@Override
	public abstract boolean isRequestedSize(int attemptWidth, int attemptHeight);

	@Override
	@Nullable
	public abstract BlockUtil.FoundRectangle getRectangle();

	@Override
	@Nullable
	public abstract BlockPos doesPortalFitAt(LevelAccessor level, BlockPos attemptPos, Direction.Axis axis);

	@Override
	public abstract void lightPortal(Block frameBlock);

	@Override
	public abstract void createPortal(Level level, BlockPos pos, BlockState frameBlock, Direction.Axis axis);

	@Override
	public abstract Vec3 getEntityOffsetInPortal(BlockUtil.FoundRectangle arg, Entity entity);

	@Override
	public abstract TeleportTransition getTeleportTargetInPortal(
			ServerLevel serverLevel,
			BlockUtil.FoundRectangle portalRect,
			Axis portalAxis,
			Vec3 prevOffset,
			Entity entity,
			PortalLink link
	);

	@Nullable
	protected BlockPos getLowerCorner(BlockPos blockPos, Direction.Axis axis1, Direction.Axis axis2) {
		if (!validStateInsidePortal(levelAccessor.getBlockState(blockPos), VALID_FRAME)) {
			return null;
		}

		return getLimitForAxis(getLimitForAxis(blockPos, axis1), axis2);
	}

	@Nullable
	protected BlockPos getLimitForAxis(@Nullable BlockPos blockPos, @Nullable Direction.Axis axis) {
		if (blockPos == null || axis == null) {
			return null;
		}

		int offset = 1;
		while (validStateInsidePortal(levelAccessor.getBlockState(blockPos.relative(axis, -offset)), VALID_FRAME)) {
			offset++;
			if (offset > PortalConstants.MAX_FRAME_CHECK_DISTANCE)
				return null;
			if (
					(axis.equals(Direction.Axis.Y) && blockPos.getY() - offset < levelAccessor.getMinY())
							|| (!axis.equals(Direction.Axis.Y)
							&& !levelAccessor.getWorldBorder().isWithinBounds(blockPos.relative(axis, -offset)))
			) {

				return null;
			}
		}

		return blockPos.relative(axis, -(offset - 1));
	}

	protected int getSize(Direction.Axis axis, int minSize, int maxSize) {
		if (lowerCorner == null) {
			return 0;
		}

		for (int i = 1; i <= maxSize; i++) {
			BlockState blockState = levelAccessor.getBlockState(lowerCorner.relative(axis, i));
			if (!validStateInsidePortal(blockState, VALID_FRAME)) {
				if (VALID_FRAME.contains(blockState.getBlock())) {
					return i >= minSize ? i : 0;

				}
				break;
			}
		}

		return 0;
	}

	protected boolean checkForValidFrame(Direction.Axis axis1, Direction.Axis axis2, int size1, int size2) {
		if (lowerCorner == null) {
			return false;
		}

		BlockPos checkPos = lowerCorner.mutable();
		if (frameContainsBlock(axis1, axis2, size1, size2, checkPos)) {
			return false;
		}

		checkPos = lowerCorner.mutable();
		return !frameContainsBlock(axis2, axis1, size2, size1, checkPos);
	}

	private boolean frameContainsBlock(Axis axis1, Axis axis2, int size1, int size2, BlockPos checkPos) {
		for (int i = 0; i < size1; i++) {
			if (
					!VALID_FRAME.contains(levelAccessor.getBlockState(checkPos.relative(axis2, -1)).getBlock())
							|| !VALID_FRAME.contains(levelAccessor.getBlockState(checkPos.relative(axis2, size2)).getBlock())
			) {

				return true;
			}

			checkPos = checkPos.relative(axis1, 1);
		}

		return false;
	}

	protected void countExistingPortalBlocks(Direction.Axis axis1, Direction.Axis axis2, int size1, int size2) {
		if (lowerCorner == null) {
			return;
		}

		for (int i = 0; i < size1; i++) {
			for (int j = 0; j < size2; j++) {
				if (levelAccessor.getBlockState(lowerCorner.relative(axis1, i).relative(axis2, j)).getBlock() instanceof CustomPortalBlock) {
					foundPortalBlocks++;
				}
			}
		}
	}

	protected BlockState blockWithAxis(BlockState state, Direction.Axis axis) {
		if (state.getBlock() instanceof CustomPortalBlock) {
			return state.setValue(CustomPortalBlock.AXIS, axis);
		}
		return state;
	}

	/**
	 * Fills air around the portal if the block is solid.
	 *
	 * @param level the level to modify
	 * @param pos   the position to check and potentially fill with air
	 */
	protected void fillAirAroundPortal(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.isSolid() || state.isRedstoneConductor(level, pos)) {
			level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
		}
	}

	/**
	 * Places a landing pad block if the current block is not solid.
	 *
	 * @param level      the level to modify
	 * @param pos        the position to place the landing pad
	 * @param frameBlock the frame block to use as landing pad
	 */
	protected void placeLandingPad(Level level, BlockPos pos, BlockState frameBlock) {
		if (!level.getBlockState(pos).isSolid()) {
			level.setBlockAndUpdate(pos, frameBlock);
		}
	}
}