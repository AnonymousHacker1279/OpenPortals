package tech.anonymoushacker1279.openportals.portal.config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import tech.anonymoushacker1279.openportals.portal.PortalIgnitionSource;
import tech.anonymoushacker1279.openportals.util.PortalUtils;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Handles events and visual effects for portals. Manages teleportation events, ignition events, and particle effects.
 */
public class PortalEventHandler {

	// Events
	private final Consumer<Entity> postTeleportEvent;
	private final Function<Entity, Boolean> preTeleportEvent;
	private final BiFunction<BlockPos, PortalIgnitionSource, Boolean> prePortalIgniteEvent;
	private final BiConsumer<BlockPos, PortalIgnitionSource> postPortalIgniteEvent;

	// Visual effects
	private final BiFunction<Level, BlockPos, ParticleOptions> portalParticle;

	/**
	 * Package-private constructor - use {@link Builder}.
	 */
	PortalEventHandler(Builder builder) {
		this.postTeleportEvent = builder.postTeleportEvent;
		this.preTeleportEvent = builder.preTeleportEvent;
		this.prePortalIgniteEvent = builder.prePortalIgniteEvent;
		this.postPortalIgniteEvent = builder.postPortalIgniteEvent;
		this.portalParticle = builder.portalParticle;
	}

	public Function<Entity, Boolean> getPreTeleportEvent() {
		return preTeleportEvent;
	}

	public BiFunction<BlockPos, PortalIgnitionSource, Boolean> getPrePortalIgniteEvent() {
		return prePortalIgniteEvent;
	}

	public BiConsumer<BlockPos, PortalIgnitionSource> getPostPortalIgniteEvent() {
		return postPortalIgniteEvent;
	}

	public BiFunction<Level, BlockPos, ParticleOptions> getPortalParticle() {
		return portalParticle;
	}

	/**
	 * Execute the post-teleport event for the given entity.
	 *
	 * @param entity the entity that was teleported
	 */
	public void executePostTeleportEvent(Entity entity) {
		postTeleportEvent.accept(entity);
	}

	/**
	 * Builder for creating {@link PortalEventHandler} instances.
	 */
	public static class Builder {
		private Consumer<Entity> postTeleportEvent = entity -> {
		};
		private Function<Entity, Boolean> preTeleportEvent = entity -> true;
		private BiFunction<BlockPos, PortalIgnitionSource, Boolean> prePortalIgniteEvent = (pos, source) -> true;
		private BiConsumer<BlockPos, PortalIgnitionSource> postPortalIgniteEvent = (pos, source) -> {
		};

		private BiFunction<Level, BlockPos, ParticleOptions> portalParticle = (level, pos) -> new BlockParticleOption(
				ParticleTypes.BLOCK,
				PortalUtils.getPortalBase(level, pos).defaultBlockState());

		public void postTeleportEvent(Consumer<Entity> event) {
			this.postTeleportEvent = event;
		}

		public void preTeleportEvent(Function<Entity, Boolean> event) {
			this.preTeleportEvent = event;
		}

		public void prePortalIgniteEvent(BiFunction<BlockPos, PortalIgnitionSource, Boolean> event) {
			this.prePortalIgniteEvent = event;
		}

		public void postPortalIgniteEvent(BiConsumer<BlockPos, PortalIgnitionSource> event) {
			this.postPortalIgniteEvent = event;
		}

		public void portalParticle(BiFunction<Level, BlockPos, ParticleOptions> particle) {
			this.portalParticle = particle;
		}

		public PortalEventHandler build() {
			return new PortalEventHandler(this);
		}
	}
}