package tech.anonymoushacker1279.openportals.portal.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.portal.PortalLink;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;
import tech.anonymoushacker1279.openportals.util.PortalConstants;

/**
 * Handles searching for suitable locations and physically building portal structures.
 */
public class PortalPlacer {

	/**
	 * Find a suitable location and build a destination portal. Searches in a spiral pattern around the target position,
	 * checking for solid ground.
	 *
	 * @param serverLevel the level to build the portal in
	 * @param targetPos   the target position to search around
	 * @param frameBlock  the block state to use for the portal frame
	 * @param axis        the axis orientation of the portal
	 * @param link        the portal configuration
	 * @return the bounds of the created portal, or null if creation failed
	 */
	@Nullable
	public static BlockUtil.FoundRectangle findAndBuildPortal(ServerLevel serverLevel,
	                                                          BlockPos targetPos,
	                                                          BlockState frameBlock,
	                                                          Direction.Axis axis,
	                                                          PortalLink link) {

		PortalFrameTester frameTester = link.getFrameTester();

		// Determine Y search bounds based on dimension
		int topY = calculateTopY(serverLevel, link);
		int bottomY = calculateBottomY(serverLevel, link);

		// Search in a spiral pattern around target position
		for (BlockPos.MutableBlockPos mutable : BlockPos.spiralAround(
				targetPos,
				PortalConstants.PORTAL_SEARCH_RADIUS,
				Direction.WEST,
				Direction.SOUTH)) {

			BlockPos testingPos = mutable.immutable();

			// Skip if outside world border
			if (!serverLevel.getWorldBorder().isWithinBounds(testingPos)) {
				continue;
			}

			// Search vertically from top to bottom
			for (int y = topY; y >= bottomY; y--) {
				if (serverLevel.getBlockState(testingPos.atY(y)).isSolid()) {
					// Found solid ground, check if portal fits above it
					BlockPos portalPos = frameTester.doesPortalFitAt(
							serverLevel,
							testingPos.atY(y + 1),
							axis
					);

					if (portalPos != null) {
						// Portal fits here, create it
						frameTester.createPortal(serverLevel, portalPos, frameBlock, axis);
						return frameTester.getRectangle();
					}
				}
			}
		}

		// No suitable location found, create portal at target position as fallback
		frameTester.createPortal(serverLevel, targetPos, frameBlock, axis);
		return frameTester.getRectangle();
	}

	/**
	 * Calculate the top Y coordinate for portal placement search.
	 */
	private static int calculateTopY(ServerLevel level, PortalLink link) {
		int defaultTopY = Math.min(level.getMaxY(), level.getMinY() + level.getLogicalHeight()) - PortalConstants.PORTAL_Y_SEARCH_MARGIN;

		// Check if in target dimension
		if (level.dimension().identifier().equals(link.getTargetDimensionIdentifier())) {
			return link.getPortalSearchYTop() != Integer.MIN_VALUE
					? link.getPortalSearchYTop()
					: defaultTopY;
		} else {
			return link.getReturnPortalSearchYTop() != Integer.MIN_VALUE
					? link.getReturnPortalSearchYTop()
					: defaultTopY;
		}
	}

	/**
	 * Calculate the bottom Y coordinate for portal placement search.
	 */
	private static int calculateBottomY(ServerLevel level, PortalLink link) {
		int defaultBottomY = level.getMinY() + PortalConstants.PORTAL_Y_SEARCH_MARGIN;

		// Check if in target dimension
		if (level.dimension().identifier().equals(link.getTargetDimensionIdentifier())) {
			return link.getPortalSearchYBottom() != Integer.MIN_VALUE
					? link.getPortalSearchYBottom()
					: defaultBottomY;
		} else {
			return link.getReturnPortalSearchYBottom() != Integer.MIN_VALUE
					? link.getReturnPortalSearchYBottom()
					: defaultBottomY;
		}
	}
}