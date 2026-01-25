package tech.anonymoushacker1279.openportals.portal.frame.api;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import tech.anonymoushacker1279.openportals.portal.PortalLink;

/**
 * Interface for calculating teleportation destinations and offsets. Responsible for determining where entities should
 * be teleported within portals.
 */
public interface PortalTeleporter {

	/**
	 * Calculate the entity's offset within the source portal. This is used to preserve relative position when
	 * teleporting.
	 *
	 * @param portalRect the rectangular bounds of the portal
	 * @param entity     the entity entering the portal
	 * @return the entity's offset within the portal
	 */
	Vec3 getEntityOffsetInPortal(BlockUtil.FoundRectangle portalRect, Entity entity);

	/**
	 * Calculate the teleportation target within the destination portal.
	 *
	 * @param serverLevel the destination level
	 * @param portalRect  the rectangular bounds of the destination portal
	 * @param portalAxis  the axis of the portal
	 * @param prevOffset  the entity's offset from the source portal
	 * @param entity      the entity being teleported
	 * @param link        the portal configuration link
	 * @return the teleport transition describing the destination
	 */
	TeleportTransition getTeleportTargetInPortal(
			ServerLevel serverLevel,
			BlockUtil.FoundRectangle portalRect,
			Direction.Axis portalAxis,
			Vec3 prevOffset,
			Entity entity,
			PortalLink link
	);
}