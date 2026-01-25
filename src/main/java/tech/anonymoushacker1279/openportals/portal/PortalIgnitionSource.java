package tech.anonymoushacker1279.openportals.portal;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a source that can ignite a portal. Supports items, fluids, block placement, and custom sources.
 */
public class PortalIgnitionSource {

	public enum SourceType {
		USE_ITEM,
		BLOCK_PLACED,
		FLUID,
		CUSTOM
	}

	public static final PortalIgnitionSource FIRE = new PortalIgnitionSource(
			SourceType.BLOCK_PLACED,
			BuiltInRegistries.BLOCK.getKey(Blocks.FIRE));
	public static final PortalIgnitionSource WATER = fromFluid(Fluids.WATER);

	private static final Set<Item> REGISTERED_ITEMS = ConcurrentHashMap.newKeySet();

	public final SourceType sourceType;
	public final Identifier ignitionSourceID;

	@Nullable
	public Player player;

	private PortalIgnitionSource(SourceType sourceType, Identifier ignitionSourceID) {
		this.sourceType = sourceType;
		this.ignitionSourceID = ignitionSourceID;
	}

	/**
	 * Create an ignition source from an item. Registers the item so it can be detected in event handlers.
	 *
	 * @param item the item to use as an ignition source
	 * @return a new PortalIgnitionSource
	 */
	public static PortalIgnitionSource fromItem(Item item) {
		REGISTERED_ITEMS.add(item);
		return new PortalIgnitionSource(SourceType.USE_ITEM, BuiltInRegistries.ITEM.getKey(item));
	}

	/**
	 * Create an ignition source from a fluid.
	 *
	 * @param fluid the fluid to use as an ignition source
	 * @return a new PortalIgnitionSource
	 */
	public static PortalIgnitionSource fromFluid(Fluid fluid) {
		return new PortalIgnitionSource(SourceType.FLUID, BuiltInRegistries.FLUID.getKey(fluid));
	}

	/**
	 * Create a custom ignition source. You must manually trigger portal ignition for custom sources.
	 *
	 * @param ignitionSourceID the identifier for this custom source
	 * @return a new PortalIgnitionSource
	 */
	public static PortalIgnitionSource fromCustomSource(Identifier ignitionSourceID) {
		return new PortalIgnitionSource(SourceType.CUSTOM, ignitionSourceID);
	}

	/**
	 * Check if an item is registered as an ignition source.
	 *
	 * @param item the item to check
	 * @return true if the item is registered
	 */
	public static boolean isRegisteredIgnitionSourceWith(Item item) {
		return REGISTERED_ITEMS.contains(item);
	}

	/**
	 * Associate a player with this ignition source. Used to track who ignited the portal.
	 *
	 * @param player the player who used this ignition source
	 * @return this ignition source for method chaining
	 */
	public PortalIgnitionSource withPlayer(Player player) {
		this.player = player;
		return this;
	}

	/**
	 * Check if this ignition source is water or a water-based fluid.
	 *
	 * @return true if this is a water fluid
	 */
	public boolean isWater() {
		return BuiltInRegistries.FLUID.get(ignitionSourceID)
				.map(holder -> holder.is(FluidTags.WATER))
				.orElse(false);
	}

	/**
	 * Check if this ignition source is lava or a lava-based fluid.
	 *
	 * @return true if this is a lava fluid
	 */
	public boolean isLava() {
		return BuiltInRegistries.FLUID.get(ignitionSourceID)
				.map(holder -> holder.is(FluidTags.LAVA))
				.orElse(false);
	}
}