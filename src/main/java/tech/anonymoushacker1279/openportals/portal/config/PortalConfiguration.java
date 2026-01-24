package tech.anonymoushacker1279.openportals.portal.config;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalIgnitionSource;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;
import tech.anonymoushacker1279.openportals.portal.frame.VerticalPortalFrameTester;

/**
 * Immutable configuration data for a custom portal.
 */
public class PortalConfiguration {

	// Core portal settings
	private final Block frameBlock;
	private final PortalIgnitionSource ignitionSource;
	private final CustomPortalBlock portalBlock;
	private final Identifier targetDimensionIdentifier;
	private final Identifier returnDimensionIdentifier;
	private final int color;

	// Portal size constraints
	private final int strictWidth;
	private final int strictHeight;

	// Portal search parameters
	private final int portalSearchYBottom;
	private final int portalSearchYTop;
	private final int returnPortalSearchYBottom;
	private final int returnPortalSearchYTop;

	// Frame validation
	private final PortalFrameTester portalFrameTester;

	/**
	 * Package-private constructor - use {@link Builder}.
	 */
	PortalConfiguration(Builder builder) {
		// Validate required fields
		if (builder.frameBlock == null) {
			throw new IllegalStateException("Frame block must be set");
		}
		if (builder.ignitionSource == null) {
			throw new IllegalStateException("Ignition source must be set");
		}
		if (builder.portalBlock == null) {
			throw new IllegalStateException("Portal block must be set");
		}
		if (builder.targetDimensionIdentifier == null) {
			throw new IllegalStateException("Target dimension must be set");
		}
		if (builder.returnDimensionIdentifier == null) {
			throw new IllegalStateException("Return dimension must be set");
		}
		if (builder.portalFrameTester == null) {
			throw new IllegalStateException("Portal frame tester must be set");
		}

		this.frameBlock = builder.frameBlock;
		this.ignitionSource = builder.ignitionSource;
		this.portalBlock = builder.portalBlock;
		this.targetDimensionIdentifier = builder.targetDimensionIdentifier;
		this.returnDimensionIdentifier = builder.returnDimensionIdentifier;
		this.color = builder.color;
		this.strictWidth = builder.strictWidth;
		this.strictHeight = builder.strictHeight;
		this.portalSearchYBottom = builder.portalSearchYBottom;
		this.portalSearchYTop = builder.portalSearchYTop;
		this.returnPortalSearchYBottom = builder.returnPortalSearchYBottom;
		this.returnPortalSearchYTop = builder.returnPortalSearchYTop;
		this.portalFrameTester = builder.portalFrameTester;
	}

	// Getters
	public Block getFrameBlock() {
		return frameBlock;
	}

	public PortalIgnitionSource getIgnitionSource() {
		return ignitionSource;
	}

	public CustomPortalBlock getPortalBlock() {
		return portalBlock;
	}

	public Identifier getTargetDimensionIdentifier() {
		return targetDimensionIdentifier;
	}

	public Identifier getReturnDimensionIdentifier() {
		return returnDimensionIdentifier;
	}

	public int getColor() {
		return color;
	}

	public int getStrictWidth() {
		return strictWidth;
	}

	public int getStrictHeight() {
		return strictHeight;
	}

	public int getPortalSearchYBottom() {
		return portalSearchYBottom;
	}

	public int getPortalSearchYTop() {
		return portalSearchYTop;
	}

	public int getReturnPortalSearchYBottom() {
		return returnPortalSearchYBottom;
	}

	public int getReturnPortalSearchYTop() {
		return returnPortalSearchYTop;
	}

	public PortalFrameTester getFrameTester() {
		return portalFrameTester;
	}

	/**
	 * Check if the given ignition source matches this portal's ignition source.
	 *
	 * @param attemptedSource the ignition source to check
	 * @return true if the sources match
	 */
	public boolean doesIgnitionMatch(PortalIgnitionSource attemptedSource) {
		return ignitionSource.sourceType == attemptedSource.sourceType
				&& ignitionSource.ignitionSourceID.equals(attemptedSource.ignitionSourceID);
	}

	/**
	 * Check if this portal can be lit in the given dimension.
	 *
	 * @param identifier the dimension identifier to check
	 * @return true if the portal can be lit in this dimension
	 */
	public boolean canLightInDimension(Identifier identifier) {
		return identifier.equals(returnDimensionIdentifier) || identifier.equals(targetDimensionIdentifier);
	}

	/**
	 * Builder for creating immutable {@link PortalConfiguration} instances.
	 */
	public static class Builder {
		private Block frameBlock;
		private PortalIgnitionSource ignitionSource = PortalIgnitionSource.FIRE;
		private CustomPortalBlock portalBlock = OpenPortals.CUSTOM_PORTAL_BLOCK.get();
		private Identifier targetDimensionIdentifier = Identifier.withDefaultNamespace("nether");
		private Identifier returnDimensionIdentifier = Identifier.withDefaultNamespace("overworld");
		private final boolean onlyIgnitableInReturnDimension = false;
		private int color = 0;
		private int strictWidth = 0;
		private int strictHeight = 0;
		private int portalSearchYBottom = Integer.MIN_VALUE;
		private int portalSearchYTop = Integer.MIN_VALUE;
		private int returnPortalSearchYBottom = Integer.MIN_VALUE;
		private int returnPortalSearchYTop = Integer.MIN_VALUE;
		private PortalFrameTester portalFrameTester = new VerticalPortalFrameTester();

		public void frameBlock(Block frameBlock) {
			this.frameBlock = frameBlock;
		}

		public void ignitionSource(PortalIgnitionSource ignitionSource) {
			this.ignitionSource = ignitionSource;
		}

		public void portalBlock(CustomPortalBlock portalBlock) {
			this.portalBlock = portalBlock;
		}

		public void targetDimension(Identifier targetDimensionIdentifier) {
			this.targetDimensionIdentifier = targetDimensionIdentifier;
		}

		public void returnDimension(Identifier returnDimensionIdentifier) {
			this.returnDimensionIdentifier = returnDimensionIdentifier;
		}

		public void color(int color) {
			this.color = color;
		}

		public void strictWidth(int strictWidth) {
			this.strictWidth = strictWidth;
		}

		public void strictHeight(int strictHeight) {
			this.strictHeight = strictHeight;
		}

		public void portalSearchYBottom(int portalSearchYBottom) {
			this.portalSearchYBottom = portalSearchYBottom;
		}

		public void portalSearchYTop(int portalSearchYTop) {
			this.portalSearchYTop = portalSearchYTop;
		}

		public void returnPortalSearchYBottom(int returnPortalSearchYBottom) {
			this.returnPortalSearchYBottom = returnPortalSearchYBottom;
		}

		public void returnPortalSearchYTop(int returnPortalSearchYTop) {
			this.returnPortalSearchYTop = returnPortalSearchYTop;
		}

		public void frameTester(PortalFrameTester portalFrameTester) {
			this.portalFrameTester = portalFrameTester;
		}

		public PortalConfiguration build() {
			return new PortalConfiguration(this);
		}
	}
}