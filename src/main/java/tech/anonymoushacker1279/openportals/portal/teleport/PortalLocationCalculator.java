package tech.anonymoushacker1279.openportals.portal.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import tech.anonymoushacker1279.openportals.util.PortalConstants;

/**
 * Calculates portal destination coordinates with dimension scaling and world border constraints.
 */
public class PortalLocationCalculator {

	/**
	 * Calculate the scaled destination position for an entity teleporting between dimensions. Applies dimension scaling
	 * and clamps to world border with safety margins.
	 *
	 * @param entity      the entity being teleported
	 * @param destination the destination level
	 * @return the calculated destination block position
	 */
	public static BlockPos calculateDestinationPosition(Entity entity, ServerLevel destination) {
		WorldBorder worldBorder = destination.getWorldBorder();

		// Calculate world border bounds with safety margins
		double xMin = Math.max(PortalConstants.WORLD_BORDER_MIN,
				worldBorder.getMinX() + PortalConstants.WORLD_BORDER_MARGIN);
		double zMin = Math.max(PortalConstants.WORLD_BORDER_MIN,
				worldBorder.getMinZ() + PortalConstants.WORLD_BORDER_MARGIN);
		double xMax = Math.min(PortalConstants.WORLD_BORDER_MAX,
				worldBorder.getMaxX() - PortalConstants.WORLD_BORDER_MARGIN);
		double zMax = Math.min(PortalConstants.WORLD_BORDER_MAX,
				worldBorder.getMaxZ() - PortalConstants.WORLD_BORDER_MARGIN);

		// Calculate dimension scale factor
		double scaleFactor = DimensionType.getTeleportationScale(
				entity.level().dimensionType(),
				destination.dimensionType()
		);

		// Apply scaling and clamp to bounds
		return BlockPos.containing(
				Mth.clamp(entity.getX() * scaleFactor, xMin, xMax),
				entity.blockPosition().getY(),
				Mth.clamp(entity.getZ() * scaleFactor, zMin, zMax)
		);
	}
}