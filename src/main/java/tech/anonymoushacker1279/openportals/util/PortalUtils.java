package tech.anonymoushacker1279.openportals.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.OpenPortals;

public class PortalUtils {

	private static final int MAX_PORTAL_BASE_SEARCH_DEPTH = PortalConstants.MAX_PORTAL_BASE_SEARCH_DEPTH;

	public static boolean isInstanceOfPortalFrame(Level level, BlockPos pos) {
		if (level.isInWorldBounds(pos)) {
			return OpenPortals.getPortalManager().isRegisteredFrameBlock(level.getBlockState(pos));
		}

		return false;
	}

	public static Block getPortalBase(Level level, BlockPos pos) {
		return getPortalBase(level, pos, 0);
	}

	private static Block getPortalBase(Level level, BlockPos pos, int depth) {
		if (depth > MAX_PORTAL_BASE_SEARCH_DEPTH) {
			return Blocks.AIR;
		}

		if (level.getBlockState(pos).getBlock() instanceof CustomPortalBlock) {
			Axis axis = getAxisFrom(level.getBlockState(pos));

			if (axis != Axis.Y) {
				if (isInstanceOfPortalFrame(level, pos.below()))
					return level.getBlockState(pos.below()).getBlock();
				if (isInstanceOfPortalFrame(level, pos.above()))
					return level.getBlockState(pos.above()).getBlock();
			} else
				axis = Direction.Axis.Z;

			if (isInstanceOfPortalFrame(level, pos.relative(axis, -1)))
				return level.getBlockState(pos.relative(axis, -1)).getBlock();
			if (isInstanceOfPortalFrame(level, pos.relative(axis, 1)))
				return level.getBlockState(pos.relative(axis, 1)).getBlock();

			return getPortalBase(level, pos.relative(axis, -1), depth + 1);
		} else if (isInstanceOfPortalFrame(level, pos)) {
			return level.getBlockState(pos).getBlock();
		}

		return Blocks.AIR;
	}

	public static Direction.Axis getAxisFrom(BlockState state) {
		if (state.getBlock() instanceof CustomPortalBlock)
			return state.getValue(CustomPortalBlock.AXIS);
		if (state.getBlock() instanceof NetherPortalBlock)
			return state.getValue(NetherPortalBlock.AXIS);
		if (state.getBlock() instanceof EndPortalBlock)
			return Direction.Axis.Y;
		return Axis.X;
	}
}