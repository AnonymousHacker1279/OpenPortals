package tech.anonymoushacker1279.openportals;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.event.NeoEventSubscriber;
import tech.anonymoushacker1279.openportals.portal.linking.PortalLinkingStorage;
import tech.anonymoushacker1279.openportals.util.PortalLink;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PortalManager {

	private static final HashMap<Identifier, ResourceKey<Level>> dimensions = new HashMap<>();
	private static final ConcurrentHashMap<Block, PortalLink> portals = new ConcurrentHashMap<>();
	@Nullable
	private static PortalLinkingStorage portalLinkingStorage;

	/**
	 * Add a portal frame and link to the manager.
	 *
	 * @param frameBlock the portal frame block
	 * @param link       the portal link
	 */
	public void addPortal(Block frameBlock, PortalLink link) {
		if (!dimensions.isEmpty() && !dimensions.containsKey(link.targetDimensionLocation)) {
			OpenPortals.LOGGER.error("Failed to add portal for dimension {} - dimension not found!", link.targetDimensionLocation);
		}

		if (portals.containsKey(frameBlock) || frameBlock.equals(Blocks.OBSIDIAN)) {
			OpenPortals.LOGGER.error("Failed to add portal with frame {} - a portal is already registered with this frame!", frameBlock.getName().getString());
		} else {
			portals.put(frameBlock, link);
		}
	}

	/**
	 * Populate the dimension map. Set on server start via
	 * {@link NeoEventSubscriber#onServerStart(ServerStartedEvent)}.
	 *
	 * @param registryKeys the set of dimension registry keys
	 */
	public void populateDimensions(Set<ResourceKey<Level>> registryKeys) {
		for (ResourceKey<Level> registryKey : registryKeys) {
			dimensions.put(registryKey.identifier(), registryKey);
		}
	}

	/**
	 * Check if a block state is a registered portal frame block.
	 *
	 * @param blockState the block state to check
	 * @return true if registered, false otherwise
	 */
	public boolean isRegisteredFrameBlock(BlockState blockState) {
		return portals.containsKey(blockState.getBlock());
	}

	/**
	 * Get the portal link associated with a base block.
	 *
	 * @param baseBlock the base block
	 * @return the portal link, or null if not found
	 */
	@Nullable
	public PortalLink getPortalLinkFromBase(Block baseBlock) {
		if (portals.containsKey(baseBlock)) {
			return portals.get(baseBlock);
		}

		return null;
	}

	/**
	 * Get the dimension key for a given dimension identifier.
	 *
	 * @param dimensionId the dimension identifier
	 * @return the dimension key, or null if not found
	 */
	public ResourceKey<Level> getDimensionKey(Identifier dimensionId) {
		return dimensions.get(dimensionId);
	}

	/**
	 * Set the portal linking storage. Set on server start via
	 * {@link NeoEventSubscriber#onServerStart(ServerStartedEvent)}.
	 *
	 * @param storage the portal linking storage
	 */
	public void setStorage(PortalLinkingStorage storage) {
		portalLinkingStorage = storage;
	}

	/**
	 * Get the portal linking storage. This should only be null if the server has not started yet.
	 *
	 * @return the portal linking storage, or null if not set
	 */
	@Nullable
	public PortalLinkingStorage getStorage() {
		return portalLinkingStorage;
	}
}