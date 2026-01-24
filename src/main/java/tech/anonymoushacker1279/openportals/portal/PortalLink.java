package tech.anonymoushacker1279.openportals.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.api.CustomPortalBuilder;
import tech.anonymoushacker1279.openportals.portal.config.PortalConfiguration;
import tech.anonymoushacker1279.openportals.portal.config.PortalEventHandler;
import tech.anonymoushacker1279.openportals.portal.config.PortalSoundManager;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Facade for portal configuration, sounds, and events. Composes {@link PortalConfiguration},
 * {@link PortalSoundManager}, and {@link PortalEventHandler} to provide a unified interface while maintaining
 * separation of concerns.
 * <p>
 * Use {@link Builder} to create instances via {@link CustomPortalBuilder}.
 */
public class PortalLink {

	private final PortalConfiguration configuration;
	private final PortalSoundManager soundManager;
	private final PortalEventHandler eventHandler;

	/**
	 * Package-private constructor - use {@link Builder} via {@link CustomPortalBuilder}. Direct instantiation is
	 * discouraged; use the public API instead.
	 */
	PortalLink(Builder builder) {
		this.configuration = builder.configBuilder.build();
		this.soundManager = builder.soundBuilder.build();
		this.eventHandler = builder.eventBuilder.build();
	}

	// Delegate configuration methods
	public Block getFrameBlock() {
		return configuration.getFrameBlock();
	}

	public PortalIgnitionSource getIgnitionSource() {
		return configuration.getIgnitionSource();
	}

	public CustomPortalBlock getPortalBlock() {
		return configuration.getPortalBlock();
	}

	public Identifier getTargetDimensionIdentifier() {
		return configuration.getTargetDimensionIdentifier();
	}

	public Identifier getReturnDimensionIdentifier() {
		return configuration.getReturnDimensionIdentifier();
	}

	public int getColor() {
		return configuration.getColor();
	}

	public int getStrictWidth() {
		return configuration.getStrictWidth();
	}

	public int getStrictHeight() {
		return configuration.getStrictHeight();
	}

	public int getPortalSearchYBottom() {
		return configuration.getPortalSearchYBottom();
	}

	public int getPortalSearchYTop() {
		return configuration.getPortalSearchYTop();
	}

	public int getReturnPortalSearchYBottom() {
		return configuration.getReturnPortalSearchYBottom();
	}

	public int getReturnPortalSearchYTop() {
		return configuration.getReturnPortalSearchYTop();
	}

	public PortalFrameTester getFrameTester() {
		return configuration.getFrameTester();
	}

	public boolean doesIgnitionMatch(PortalIgnitionSource attemptedSource) {
		return configuration.doesIgnitionMatch(attemptedSource);
	}

	public boolean canLightInDimension(Identifier identifier) {
		return configuration.canLightInDimension(identifier);
	}

	// Delegate sound methods
	public PortalSoundManager.SoundConfiguration<Entity> getTriggerSound() {
		return soundManager.getTriggerSound();
	}

	public PortalSoundManager.SoundConfiguration<Level> getAmbientSound() {
		return soundManager.getAmbientSound();
	}

	@Nullable
	public Identifier getTriggerSoundIdentifier() {
		return soundManager.getTriggerSoundIdentifier();
	}

	@Nullable
	public Identifier getAmbientSoundIdentifier() {
		return soundManager.getAmbientSoundIdentifier();
	}

	public void playTravelSound(Entity entity) {
		soundManager.playTravelSound(entity);
	}

	// Delegate event methods
	public Function<Entity, Boolean> getPreTeleportEvent() {
		return eventHandler.getPreTeleportEvent();
	}

	public BiFunction<BlockPos, PortalIgnitionSource, Boolean> getPrePortalIgniteEvent() {
		return eventHandler.getPrePortalIgniteEvent();
	}

	public BiConsumer<BlockPos, PortalIgnitionSource> getPostPortalIgniteEvent() {
		return eventHandler.getPostPortalIgniteEvent();
	}

	public BiFunction<Level, BlockPos, ParticleOptions> getPortalParticle() {
		return eventHandler.getPortalParticle();
	}

	public void executePostTeleportEvent(Entity entity) {
		eventHandler.executePostTeleportEvent(entity);
	}

	/**
	 * Builder class for creating immutable {@link PortalLink} instances. Use via {@link CustomPortalBuilder} as this
	 * has no stability guarantees.
	 */
	@ApiStatus.Internal
	public static class Builder {
		private final PortalConfiguration.Builder configBuilder = new PortalConfiguration.Builder();
		private final PortalSoundManager.Builder soundBuilder = new PortalSoundManager.Builder();
		private final PortalEventHandler.Builder eventBuilder = new PortalEventHandler.Builder();

		// Configuration setters
		public void frameBlock(Block frameBlock) {
			configBuilder.frameBlock(frameBlock);
		}

		public void ignitionSource(PortalIgnitionSource ignitionSource) {
			configBuilder.ignitionSource(ignitionSource);
		}

		public void portalBlock(CustomPortalBlock portalBlock) {
			configBuilder.portalBlock(portalBlock);
		}

		public void targetDimension(Identifier targetDimensionIdentifier) {
			configBuilder.targetDimension(targetDimensionIdentifier);
		}

		public void returnDimension(Identifier returnDimensionIdentifier) {
			configBuilder.returnDimension(returnDimensionIdentifier);
		}

		public void color(int color) {
			configBuilder.color(color);
		}

		public void strictWidth(int strictWidth) {
			configBuilder.strictWidth(strictWidth);
		}

		public void strictHeight(int strictHeight) {
			configBuilder.strictHeight(strictHeight);
		}

		public void portalSearchYBottom(int portalSearchYBottom) {
			configBuilder.portalSearchYBottom(portalSearchYBottom);
		}

		public void portalSearchYTop(int portalSearchYTop) {
			configBuilder.portalSearchYTop(portalSearchYTop);
		}

		public void returnPortalSearchYBottom(int returnPortalSearchYBottom) {
			configBuilder.returnPortalSearchYBottom(returnPortalSearchYBottom);
		}

		public void returnPortalSearchYTop(int returnPortalSearchYTop) {
			configBuilder.returnPortalSearchYTop(returnPortalSearchYTop);
		}

		public void frameTester(PortalFrameTester portalFrameTester) {
			configBuilder.frameTester(portalFrameTester);
		}

		// Sound setters
		public void travelSound(Identifier soundIdentifier, Function<Entity, Float> volume, Function<Entity, Float> pitch) {
			soundBuilder.travelSound(soundIdentifier, volume, pitch);
		}

		public void triggerSound(Identifier soundIdentifier, Function<Entity, Float> volume, Function<Entity, Float> pitch) {
			soundBuilder.triggerSound(soundIdentifier, volume, pitch);
		}

		public void ambientSound(Identifier soundIdentifier, Function<Level, Float> volume, Function<Level, Float> pitch) {
			soundBuilder.ambientSound(soundIdentifier, volume, pitch);
		}

		// Event setters
		public void postTeleportEvent(Consumer<Entity> event) {
			eventBuilder.postTeleportEvent(event);
		}

		public void preTeleportEvent(Function<Entity, Boolean> event) {
			eventBuilder.preTeleportEvent(event);
		}

		public void prePortalIgniteEvent(BiFunction<BlockPos, PortalIgnitionSource, Boolean> event) {
			eventBuilder.prePortalIgniteEvent(event);
		}

		public void postPortalIgniteEvent(BiConsumer<BlockPos, PortalIgnitionSource> event) {
			eventBuilder.postPortalIgniteEvent(event);
		}

		public void portalParticle(BiFunction<Level, BlockPos, ParticleOptions> particle) {
			eventBuilder.portalParticle(particle);
		}

		/**
		 * Build the immutable {@link PortalLink} instance.
		 *
		 * @return the built PortalLink
		 */
		public PortalLink build() {
			return new PortalLink(this);
		}
	}
}