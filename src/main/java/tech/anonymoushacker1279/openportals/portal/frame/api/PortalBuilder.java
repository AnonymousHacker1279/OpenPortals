package tech.anonymoushacker1279.openportals.portal.frame.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for building and lighting portal structures. Responsible for creating portal blocks and modifying the world
 * to form portals.
 */
public interface PortalBuilder {

	/**
	 * Light the portal by filling it with portal blocks.
	 *
	 * @param frameBlock the block that serves as the portal frame
	 */
	void lightPortal(Block frameBlock);

	/**
	 * Create a complete portal structure at the given position. This includes placing the frame and portal blocks.
	 *
	 * @param level      the level to create the portal in
	 * @param pos        the position to create the portal at
	 * @param frameBlock the block state to use for the frame
	 * @param axis       the axis along which the portal should be oriented
	 */
	void createPortal(Level level, BlockPos pos, BlockState frameBlock, Direction.Axis axis);
}