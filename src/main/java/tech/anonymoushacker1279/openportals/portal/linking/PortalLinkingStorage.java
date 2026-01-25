package tech.anonymoushacker1279.openportals.portal.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores bidirectional links between portal locations across dimensions.
 */
public class PortalLinkingStorage extends SavedData {

	public static final Codec<PortalLinkingStorage> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					DimensionLink.CODEC.listOf().fieldOf("dimensionLinks").forGetter(PortalLinkingStorage::getDimensionLinks)
			).apply(instance, PortalLinkingStorage::new)
	);

	public static final SavedDataType<PortalLinkingStorage> TYPE = new SavedDataType<>(
			"openportals_dimension_links",
			PortalLinkingStorage::new,
			CODEC,
			null
	);

	private final List<DimensionLink> dimensionLinks = new ArrayList<>();

	public PortalLinkingStorage() {
	}

	public PortalLinkingStorage(List<DimensionLink> portalLinks) {
		this.dimensionLinks.addAll(portalLinks);
	}

	public List<DimensionLink> getDimensionLinks() {
		return dimensionLinks;
	}

	/**
	 * Get the destination for a portal at the given position.
	 *
	 * @param portalFramePos the position of the portal frame
	 * @param dim            the dimension of the portal
	 * @return the destination position, or null if no link exists
	 */
	@Nullable
	public DimensionalBlockPos getDestination(BlockPos portalFramePos, ResourceKey<Level> dim) {
		DimensionalBlockPos source = new DimensionalBlockPos(dim.identifier(), portalFramePos);

		for (DimensionLink link : dimensionLinks) {
			DimensionalBlockPos destination = link.getDestination(source);
			if (destination != null) {
				return destination;
			}
		}

		return null;
	}

	/**
	 * Create a bidirectional link between two portals. Only adds a single link object that works in both directions.
	 *
	 * @param portalFramePos     the position of the source portal frame
	 * @param fromDim            the dimension of the source portal
	 * @param destPortalFramePos the position of the destination portal frame
	 * @param destDim            the dimension of the destination portal
	 */
	public void createLink(BlockPos portalFramePos, ResourceKey<Level> fromDim, BlockPos destPortalFramePos, ResourceKey<Level> destDim) {
		DimensionalBlockPos pos1 = new DimensionalBlockPos(fromDim.identifier(), portalFramePos);
		DimensionalBlockPos pos2 = new DimensionalBlockPos(destDim.identifier(), destPortalFramePos);

		// Check if a link already exists between these two positions
		for (DimensionLink existingLink : dimensionLinks) {
			if (existingLink.connects(pos1) && existingLink.connects(pos2)) {
				return; // Link already exists, no need to add
			}
		}

		// Add new bidirectional link
		dimensionLinks.add(new DimensionLink(pos1, pos2));
		setDirty();
	}

	/**
	 * Check if a link should be cleaned up. Validates that both portals in the bidirectional link still exist.
	 *
	 * @param link   the link to check
	 * @param server the server to check dimensions
	 * @return true if the link should be removed
	 */
	private boolean shouldCleanupLink(DimensionLink link, MinecraftServer server) {
		// Check first portal (pos1)
		ServerLevel level1 = server.getLevel(ResourceKey.create(
				Registries.DIMENSION,
				link.pos1().dimension()));

		if (level1 == null) {
			return true; // Dimension doesn't exist
		}

		// Check if there's a portal block at pos1
		if (!(level1.getBlockState(link.pos1().pos()).getBlock() instanceof CustomPortalBlock)) {
			return true; // Portal at pos1 is gone
		}

		// Check second portal (pos2)
		ServerLevel level2 = server.getLevel(ResourceKey.create(
				Registries.DIMENSION,
				link.pos2().dimension()));

		if (level2 == null) {
			return true; // Dimension doesn't exist
		}

		// Check if there's a portal block at pos2
		return !(level2.getBlockState(link.pos2().pos()).getBlock() instanceof CustomPortalBlock);
	}

	/**
	 * Clean up orphaned links.
	 *
	 * @param server the server to check dimensions
	 * @return the number of links removed
	 */
	public int cleanupOrphanedLinks(MinecraftServer server) {
		int removed = 0;
		Iterator<DimensionLink> iterator = dimensionLinks.iterator();

		while (iterator.hasNext()) {
			DimensionLink link = iterator.next();
			if (shouldCleanupLink(link, server)) {
				iterator.remove();
				removed++;
			}
		}

		if (removed > 0) {
			setDirty();
		}

		return removed;
	}
}