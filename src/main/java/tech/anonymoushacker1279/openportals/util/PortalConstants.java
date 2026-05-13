package tech.anonymoushacker1279.openportals.util;

/**
 * Constants used throughout the portal system. Centralizes magic numbers for easier maintenance and configuration.
 */
public final class PortalConstants {

	/**
	 * Maximum width for vanilla-style (vertical) portals.
	 */
	public static final int MAX_PORTAL_WIDTH = 21;

	// Portal Size Limits
	/**
	 * Maximum height for vanilla-style (vertical) portals.
	 */
	public static final int MAX_PORTAL_HEIGHT = 21;
	/**
	 * Minimum width for vanilla-style portals.
	 */
	public static final int MIN_PORTAL_WIDTH = 2;
	/**
	 * Minimum height for vanilla-style portals.
	 */
	public static final int MIN_PORTAL_HEIGHT = 3;
	/**
	 * Maximum size for flat (horizontal) portals in the X direction.
	 */
	public static final int MAX_FLAT_PORTAL_X_SIZE = 21;
	/**
	 * Maximum size for flat (horizontal) portals in the Z direction.
	 */
	public static final int MAX_FLAT_PORTAL_Z_SIZE = 21;
	/**
	 * Minimum size for flat portals (both X and Z).
	 */
	public static final int MIN_FLAT_PORTAL_SIZE = 2;
	/**
	 * Maximum search radius when looking for a valid portal location (in blocks).
	 */
	public static final int PORTAL_SEARCH_RADIUS = 32;

	// Portal Search Parameters
	/**
	 * Maximum recursion depth when searching for portal base block.
	 */
	public static final int MAX_PORTAL_BASE_SEARCH_DEPTH = 20;
	/**
	 * Y-offset margin from world height limits when searching for portal placement.
	 */
	public static final int PORTAL_Y_SEARCH_MARGIN = 5;
	/**
	 * Minimum coordinate value for portal placement (world border safety).
	 */
	public static final double WORLD_BORDER_MIN = -2.9999872E7D;

	// World Border Safety Margins
	/**
	 * Maximum coordinate value for portal placement (world border safety).
	 */
	public static final double WORLD_BORDER_MAX = 2.9999872E7D;
	/**
	 * Safety margin from world border (in blocks).
	 */
	public static final double WORLD_BORDER_MARGIN = 16.0D;
	/**
	 * Chance (1 in N) for ambient sound to play each tick.
	 */
	public static final int AMBIENT_SOUND_CHANCE = 100;

	// Portal Animation & Effects
	/**
	 * Number of particles to spawn per portal block per tick.
	 */
	public static final int PARTICLES_PER_TICK = 4;
	/**
	 * Maximum number of blocks to check in any direction when validating portal frame.
	 */
	public static final int MAX_FRAME_CHECK_DISTANCE = 20;

	// Portal Detection Limits
	private PortalConstants() {
		throw new UnsupportedOperationException("Utility class");
	}
}