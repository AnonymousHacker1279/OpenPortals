package tech.anonymoushacker1279.openportals.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;
import tech.anonymoushacker1279.openportals.util.CustomPortalHelper;
import tech.anonymoushacker1279.openportals.util.PortalLink;

import java.util.Optional;

public class PortalIgniter {

	/**
	 * Attempts to light a portal at the specified position in the given level.
	 *
	 * @param level          the level where the portal is to be lit
	 * @param portalPos      the position of the portal to be lit
	 * @param ignitionSource the source of ignition for the portal
	 * @return true if the portal was successfully lit, false otherwise
	 */
	public static boolean attemptPortalLight(Level level, BlockPos portalPos, PortalIgnitionSource ignitionSource) {
		return attemptPortalLight(level, portalPos, CustomPortalHelper.getClosestFrameBlock(level, portalPos), ignitionSource);
	}

	/**
	 * Attempts to light a portal at the specified position in the given level.
	 *
	 * @param level          the level where the portal is to be lit
	 * @param portalPos      the position of the portal to be lit
	 * @param framePos       the position of the frame block for the portal
	 * @param ignitionSource the source of ignition for the portal
	 * @return true if the portal was successfully lit, false otherwise
	 */
	public static boolean attemptPortalLight(Level level, BlockPos portalPos, BlockPos framePos, PortalIgnitionSource ignitionSource) {
		Block foundationBlock = level.getBlockState(framePos).getBlock();
		PortalLink link = OpenPortals.getPortalLinkFromBase(foundationBlock);

		if (link == null || !link.doesIgnitionMatch(ignitionSource) || !link.canLightInDim(level.dimension().identifier())) {
			return false;
		}

		return attemptToLightPortal(link, level, portalPos, foundationBlock, ignitionSource);
	}

	/**
	 * Actually perform the lighting of the portal.
	 *
	 * @param link            the {@link PortalLink} associated with the portal
	 * @param level           the level where the portal is to be lit
	 * @param pos             the position of the portal to be lit
	 * @param foundationBlock the block that serves as the foundation for the portal
	 * @param ignitionSource  the source of ignition for the portal
	 * @return true if the portal was successfully lit, false otherwise
	 */
	private static boolean attemptToLightPortal(
			PortalLink link,
			Level level,
			BlockPos pos,
			Block foundationBlock,
			PortalIgnitionSource ignitionSource
	) {
		Optional<PortalFrameTester> optional = link.getFrameTester().getNewPortal(level, pos, Direction.Axis.X, foundationBlock);

		// Check for valid frame and correct size (if applicable)
		if (optional.isPresent()) {
			if (
					optional.get().isRequestedSize(link.strictWidth, link.strictHeight) && link.getPrePortalIgniteEvent()
							.apply(pos, ignitionSource)
			) {
				optional.get().lightPortal(foundationBlock);
				link.getPostPortalIgniteEvent().accept(pos, ignitionSource);
			}
			return true;
		}
		return false;
	}
}