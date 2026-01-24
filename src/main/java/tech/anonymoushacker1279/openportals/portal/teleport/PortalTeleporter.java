package tech.anonymoushacker1279.openportals.portal.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalLink;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;
import tech.anonymoushacker1279.openportals.portal.linking.DimensionalBlockPos;
import tech.anonymoushacker1279.openportals.util.PortalUtils;

/**
 * Main service for handling portal teleportation. Orchestrates the entire teleportation process including: -
 * Destination calculation - Portal linking lookup - Portal creation - Teleport transition generation
 */
public class PortalTeleporter {

	/**
	 * Attempt to teleport an entity through a portal.
	 *
	 * @param level      the level the entity is currently in
	 * @param entity     the entity to teleport
	 * @param portalBase the portal frame block
	 * @param portalPos  the position of the portal
	 * @return a TeleportTransition if successful, or null if teleportation should not occur
	 */
	@Nullable
	public static TeleportTransition attemptTeleport(Level level, Entity entity, Block portalBase, BlockPos portalPos) {
		// Get portal configuration
		PortalLink link = OpenPortals.getPortalManager().getPortalLinkFromBase(portalBase);
		if (link == null) {
			return null;
		}

		// Check pre-teleport event
		if (!link.getPreTeleportEvent().apply(entity)) {
			return null; // Teleportation denied by event
		}

		// Determine destination dimension
		ResourceKey<Level> destinationKey = determineDestination(level, link);
		ServerLevel destination = ((ServerLevel) level).getServer().getLevel(destinationKey);

		if (destination == null) {
			return null;
		}

		// Check if entity can use portals
		if (!entity.canUsePortal(false)) {
			return null;
		}

		return createTeleportTransition(destination, entity, portalPos, portalBase, link);
	}

	/**
	 * Determine which dimension the entity should teleport to.
	 */
	private static ResourceKey<Level> determineDestination(Level currentLevel, PortalLink link) {
		ResourceKey<Level> targetKey = OpenPortals.getPortalManager()
				.getDimensionKey(link.getTargetDimensionIdentifier());
		ResourceKey<Level> returnKey = OpenPortals.getPortalManager()
				.getDimensionKey(link.getReturnDimensionIdentifier());

		// If in target dimension, go to return dimension; otherwise go to target
		return currentLevel.dimension() == targetKey ? returnKey : targetKey;
	}

	/**
	 * Create the teleport transition for the entity.
	 */
	@Nullable
	private static TeleportTransition createTeleportTransition(ServerLevel destination,
	                                                           Entity entity,
	                                                           BlockPos enteredPortalPos,
	                                                           Block frameBlock,
	                                                           PortalLink link) {

		if (OpenPortals.getPortalManager().getStorage() == null) {
			return null;
		}

		PortalFrameTester frameTester = link.getFrameTester();
		Direction.Axis portalAxis = PortalUtils.getAxisFrom(entity.level().getBlockState(enteredPortalPos));

		// Get the source portal bounds
		BlockUtil.FoundRectangle sourcePortal = frameTester
				.init(entity.level(), enteredPortalPos, portalAxis, frameBlock)
				.getRectangle();

		if (sourcePortal == null) {
			return null;
		}

		// Try to find existing linked portal
		DimensionalBlockPos linkedPortalPos = OpenPortals.getPortalManager()
				.getStorage()
				.getDestination(sourcePortal.minCorner, entity.level().dimension());

		if (linkedPortalPos != null && linkedPortalPos.dimension().equals(destination.dimension().identifier())) {
			// Linked portal exists, use it
			TeleportTransition transition = useExistingPortal(
					destination,
					entity,
					linkedPortalPos.pos(),
					portalAxis,
					frameBlock,
					sourcePortal,
					link,
					frameTester);

			if (transition != null) {
				return transition;
			}
		}

		// No existing portal, create a new one
		return createNewPortal(destination, entity, portalAxis, sourcePortal, frameBlock, link);
	}

	/**
	 * Use an existing portal at the destination.
	 */
	@Nullable
	private static TeleportTransition useExistingPortal(ServerLevel destination,
	                                                    Entity entity,
	                                                    BlockPos portalPos,
	                                                    Direction.Axis portalAxis,
	                                                    Block frameBlock,
	                                                    BlockUtil.FoundRectangle sourcePortal,
	                                                    PortalLink link,
	                                                    PortalFrameTester frameTester) {

		// Validate and initialize destination portal
		PortalFrameTester destFrameTester = (PortalFrameTester) frameTester.init(
				destination,
				portalPos,
				portalAxis,
				frameBlock);

		if (!destFrameTester.isValidFrame()) {
			return null;
		}

		BlockUtil.FoundRectangle destPortal = destFrameTester.getRectangle();
		if (destPortal == null) {
			return null;
		}

		// Light the portal if needed
		if (!destFrameTester.isAlreadyLitPortalFrame()) {
			destFrameTester.lightPortal(frameBlock);
		}

		// Calculate teleport target
		return destFrameTester.getTeleportTargetInPortal(
				destination,
				destPortal,
				portalAxis,
				destFrameTester.getEntityOffsetInPortal(sourcePortal, entity),
				entity,
				link);
	}

	/**
	 * Create a new portal at the destination.
	 */
	private static TeleportTransition createNewPortal(ServerLevel destination,
	                                                  Entity entity,
	                                                  Direction.Axis portalAxis,
	                                                  BlockUtil.FoundRectangle sourcePortal,
	                                                  Block frameBlock,
	                                                  PortalLink link) {

		// Calculate destination position with scaling
		BlockPos targetPos = PortalLocationCalculator.calculateDestinationPosition(entity, destination);

		// Find suitable location and build portal
		BlockUtil.FoundRectangle destPortal = PortalPlacer.findAndBuildPortal(
				destination,
				targetPos,
				frameBlock.defaultBlockState(),
				portalAxis,
				link);

		if (destPortal == null) {
			// Portal creation failed, use emergency fallback
			return createEmergencyTeleport(destination, entity, targetPos);
		}

		// Link the portals
		OpenPortals.getPortalManager().getStorage().createLink(
				sourcePortal.minCorner,
				entity.level().dimension(),
				destPortal.minCorner,
				destination.dimension());

		// Calculate teleport target
		PortalFrameTester frameTester = link.getFrameTester();
		return frameTester.getTeleportTargetInPortal(
				destination,
				destPortal,
				portalAxis,
				frameTester.getEntityOffsetInPortal(sourcePortal, entity),
				entity,
				link);
	}

	/**
	 * Create an emergency teleport when portal creation fails. Places the entity on the world surface.
	 */
	private static TeleportTransition createEmergencyTeleport(ServerLevel level, Entity entity, BlockPos attemptedPos) {
		OpenPortals.LOGGER.error("Unable to find valid portal location, placing entity on world surface");

		// Force chunk load and get surface position
		level.getBlockState(attemptedPos);
		BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, attemptedPos);

		return new TeleportTransition(
				level,
				new Vec3(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5),
				entity.getDeltaMovement(),
				entity.getYRot(),
				entity.getXRot(),
				TeleportTransition.DO_NOTHING);
	}
}