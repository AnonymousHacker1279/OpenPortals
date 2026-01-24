package net.kyrptonaught.customportalapi.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.portal.linking.DimensionalBlockPos;

public class CustomTeleporter {

    /**
     * Attempts to create a {@link TeleportTransition} for the given entity based on the portal base and position. No
     * transition occurs if checks fail.
     *
     * @param level      the level in which the teleportation is occurring
     * @param entity     the entity that is being teleported
     * @param portalBase the block that serves as the base of the portal
     * @param portalPos  the position of the portal base in the level
     * @return a TeleportTransition object if the teleportation can be performed, or null if it cannot
     */
    @Nullable
    public static TeleportTransition attemptTeleport(Level level, Entity entity, Block portalBase, BlockPos portalPos) {
        PortalLink link = CustomPortalsMod.getPortalLinkFromBase(portalBase);
        if (link == null) {
            return null;
        }

        if (!link.getPreTeleportEvent().apply(entity)) {
            return null; // The entity was denied teleportation
        }

        ResourceKey<Level> destinationKey = level.dimension() == CustomPortalsMod.dimensions.get(link.targetDimensionLocation)
            ? CustomPortalsMod.dimensions.get(link.returnDimensionLocation)
            : CustomPortalsMod.dimensions.get(link.targetDimensionLocation);

        ServerLevel destination = ((ServerLevel) level).getServer().getLevel(destinationKey);
        if (destination == null) {
            return null;
        }

        if (!entity.canUsePortal(false)) {
            return null;
        }

        return createCustomTeleportTarget(destination, entity, portalPos, portalBase, link, link.getFrameTester());
    }

    /**
     * Creates a teleport target for the given entity based on the portal frame and position.
     *
     * @param destinationLevel the level to which the entity is being teleported
     * @param entity           the entity that is being teleported
     * @param enteredPortalPos the position of the portal that the entity entered
     * @param frameBlock       the block that serves as the frame of the portal
     * @param frameTester      the tester used to validate the portal frame
     * @return a TeleportTransition object if the teleportation can be performed, or null if it cannot
     */
    @Nullable
    private static TeleportTransition createCustomTeleportTarget(
        ServerLevel destinationLevel,
        Entity entity,
        BlockPos enteredPortalPos,
        Block frameBlock,
        PortalLink link,
        PortalFrameTester frameTester
    ) {
        if (CustomPortalsMod.portalLinkingStorage == null) {
            return null;
        }

        Direction.Axis portalAxis = CustomPortalHelper.getAxisFrom(entity.level().getBlockState(enteredPortalPos));
        BlockUtil.FoundRectangle fromPortalRectangle = frameTester
            .init(entity.level(), enteredPortalPos, portalAxis, frameBlock)
            .getRectangle();

        if (fromPortalRectangle == null) {
            return null;
        }

        DimensionalBlockPos destinationPos = CustomPortalsMod.portalLinkingStorage.getDestination(
            fromPortalRectangle.minCorner,
            entity.level().dimension()
        );

        if (destinationPos != null && destinationPos.dimension().equals(destinationLevel.dimension().identifier())) {
            PortalFrameTester portalFrameTester = frameTester.init(destinationLevel, destinationPos.pos(), portalAxis, frameBlock);
            if (portalFrameTester.isValidFrame() && portalFrameTester.getRectangle() != null) {
                if (!portalFrameTester.isAlreadyLitPortalFrame()) {
                    portalFrameTester.lightPortal(frameBlock);
                }

                return portalFrameTester.getTPTargetInPortal(
                    destinationLevel,
                    portalFrameTester.getRectangle(),
                    portalAxis,
                    portalFrameTester.getEntityOffsetInPortal(fromPortalRectangle, entity, portalAxis),
                    entity,
                    link
                );
            }
        }

        return createDestinationPortal(destinationLevel, entity, portalAxis, fromPortalRectangle, frameBlock.defaultBlockState(), link);
    }

    /**
     * Creates a destination portal for the given entity in the specified destination level.
     *
     * @param destination    the level to which the entity is being teleported
     * @param entity         the entity that is being teleported
     * @param axis           the axis of the portal
     * @param portalFramePos the position of the portal frame in the level
     * @param frameBlock     the block that serves as the frame of the portal
     * @return a TeleportTransition object if the teleportation can be performed, or null if it cannot
     */
    @Nullable
    private static TeleportTransition createDestinationPortal(
        ServerLevel destination,
        Entity entity,
        Direction.Axis axis,
        BlockUtil.FoundRectangle portalFramePos,
        BlockState frameBlock,
        PortalLink link
    ) {
        if (CustomPortalsMod.portalLinkingStorage == null) {
            return null;
        }

        WorldBorder worldBorder = destination.getWorldBorder();
        double xMin = Math.max(-2.9999872E7D, worldBorder.getMinX() + 16.0D);
        double zMin = Math.max(-2.9999872E7D, worldBorder.getMinZ() + 16.0D);
        double xMax = Math.min(2.9999872E7D, worldBorder.getMaxX() - 16.0D);
        double zMax = Math.min(2.9999872E7D, worldBorder.getMaxZ() - 16.0D);
        double scaleFactor = DimensionType.getTeleportationScale(entity.level().dimensionType(), destination.dimensionType());

        BlockPos blockPos = BlockPos.containing(
            Mth.clamp(entity.getX() * scaleFactor, xMin, xMax),
            entity.blockPosition().getY(),
            Mth.clamp(entity.getZ() * scaleFactor, zMin, zMax)
        );

        Optional<BlockUtil.FoundRectangle> portal = buildDestinationPortal(destination, blockPos, frameBlock, axis, link);
        if (portal.isPresent()) {
            PortalFrameTester portalFrameTester = link.getFrameTester();

            CustomPortalsMod.portalLinkingStorage.createLink(
                portalFramePos.minCorner,
                entity.level().dimension(),
                portal.get().minCorner,
                destination.dimension()
            );
            return portalFrameTester.getTPTargetInPortal(
                destination,
                portal.get(),
                axis,
                portalFrameTester.getEntityOffsetInPortal(portalFramePos, entity, axis),
                entity,
                link
            );
        }

        return failedToFindValidTarget(destination, entity, blockPos);
    }

    /**
     * Handles the case where a valid teleport location could not be found.
     *
     * @param level  the level in which the teleportation is occurring
     * @param entity the entity that is being teleported
     * @param pos    the position where the teleportation was attempted
     * @return a TeleportTransition object that places the entity at the top of the world
     */
    private static TeleportTransition failedToFindValidTarget(ServerLevel level, Entity entity, BlockPos pos) {
        CustomPortalsMod.LOGGER.error("Unable to find a valid teleport location, forced to place on top of world");
        level.getBlockState(pos); // Force load the chunk to ensure the heightmap is available
        BlockPos destinationPos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
        return new TeleportTransition(
            level,
            new Vec3(destinationPos.getX() + .5, destinationPos.getY(), destinationPos.getZ() + .5),
            entity.getDeltaMovement(),
            entity.getYRot(),
            entity.getXRot(),
            TeleportTransition.DO_NOTHING
        );
    }

    /**
     * Locate and physically build the destination portal at the specified position.
     *
     * @param serverLevel the server level where the portal is being built
     * @param blockPos    the position where the portal should be built
     * @param frameBlock  the block that serves as the frame of the portal
     * @param axis        the axis of the portal
     * @param link
     * @return an Optional containing the found rectangle of the portal if successful, or empty if not
     */
    public static Optional<BlockUtil.FoundRectangle> buildDestinationPortal(
        ServerLevel serverLevel,
        BlockPos blockPos,
        BlockState frameBlock,
        Direction.Axis axis,
        PortalLink link
    ) {
        PortalFrameTester portalFrameTester = link.getFrameTester();

        int topY = Math.min(serverLevel.getMaxY(), serverLevel.getMinY() + serverLevel.getLogicalHeight()) - 5;
        int bottomY = serverLevel.getMinY() + 5;

        if (serverLevel.dimension().identifier().equals(link.targetDimensionLocation)) {
            if (link.portalSearchYTop != Integer.MIN_VALUE) {
                topY = link.portalSearchYTop;
            }
            if (link.portalSearchYBottom != Integer.MIN_VALUE) {
                bottomY = link.portalSearchYBottom;
            }
        } else {
            if (link.returnPortalSearchYTop != Integer.MIN_VALUE) {
                topY = link.returnPortalSearchYTop;
            }
            if (link.returnPortalSearchYBottom != Integer.MIN_VALUE) {
                bottomY = link.returnPortalSearchYBottom;
            }
        }

        for (BlockPos.MutableBlockPos mutable : BlockPos.spiralAround(blockPos, 32, Direction.WEST, Direction.SOUTH)) {
            BlockPos testingPos = mutable.immutable();
            if (!serverLevel.getWorldBorder().isWithinBounds(testingPos)) {
                continue;
            }

            for (int y = topY; y >= bottomY; y--) {
                if (serverLevel.getBlockState(testingPos.atY(y)).isSolid()) {
                    BlockPos testRect = portalFrameTester.doesPortalFitAt(serverLevel, testingPos.atY(y + 1), axis);
                    if (testRect != null) {
                        portalFrameTester.createPortal(serverLevel, testRect, frameBlock, axis);
                        return Optional.ofNullable(portalFrameTester.getRectangle());
                    }
                }
            }
        }

        portalFrameTester.createPortal(serverLevel, blockPos, frameBlock, axis);
        return Optional.ofNullable(portalFrameTester.getRectangle());
    }
}