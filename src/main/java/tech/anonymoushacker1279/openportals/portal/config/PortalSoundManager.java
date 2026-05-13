package tech.anonymoushacker1279.openportals.portal.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.openportals.network.PlayerSoundPayload;

import java.util.function.Function;

/**
 * Manages sound effects for portals. Handles travel sounds, trigger sounds, and ambient sounds with configurable volume
 * and pitch.
 */
public class PortalSoundManager {

	private final SoundConfiguration<Entity> travelSound;
	private final SoundConfiguration<Entity> triggerSound;
	private final SoundConfiguration<Level> ambientSound;

	/**
	 * Package-private constructor - use {@link Builder}.
	 */
	PortalSoundManager(Builder builder) {
		this.travelSound = builder.travelSound;
		this.triggerSound = builder.triggerSound;
		this.ambientSound = builder.ambientSound;
	}

	public SoundConfiguration<Entity> getTriggerSound() {
		return triggerSound;
	}

	public SoundConfiguration<Level> getAmbientSound() {
		return ambientSound;
	}

	@Nullable
	public Identifier getTriggerSoundIdentifier() {
		return triggerSound.soundIdentifier();
	}

	@Nullable
	public Identifier getAmbientSoundIdentifier() {
		return ambientSound.soundIdentifier();
	}

	/**
	 * Play the travel sound for the given entity.
	 *
	 * @param entity the entity to play the sound for
	 */
	public void playTravelSound(Entity entity) {
		if (entity instanceof ServerPlayer player && travelSound.soundIdentifier() != null) {
			PacketDistributor.sendToPlayer(player, new PlayerSoundPayload(
					travelSound.soundIdentifier(),
					travelSound.volumeFunction().apply(entity),
					travelSound.pitchFunction().apply(entity)
			));
		}
	}

	/**
	 * Immutable sound configuration.
	 *
	 * @param soundIdentifier the identifier of the sound to play
	 * @param volumeFunction  function to determine the volume
	 * @param pitchFunction   function to determine the pitch
	 * @param <T>             the type of context object (Entity or Level)
	 */
	public record SoundConfiguration<T>(
			@Nullable Identifier soundIdentifier,
			Function<T, Float> volumeFunction,
			Function<T, Float> pitchFunction) {

		/**
		 * Create a default sound configuration with no sound.
		 */
		public static <T> SoundConfiguration<T> silent() {
			return new SoundConfiguration<>(null, context -> 0.0f, context -> 0.0f);
		}
	}

	/**
	 * Builder for creating {@link PortalSoundManager} instances.
	 */
	public static class Builder {
		private SoundConfiguration<Entity> travelSound = new SoundConfiguration<>(
				BuiltInRegistries.SOUND_EVENT.getKey(SoundEvents.PORTAL_TRAVEL),
				entity -> entity.getRandom().nextFloat() * 0.4F + 0.8F,
				entity -> 0.25f);

		private SoundConfiguration<Entity> triggerSound = new SoundConfiguration<>(
				BuiltInRegistries.SOUND_EVENT.getKey(SoundEvents.PORTAL_TRIGGER),
				entity -> entity.getRandom().nextFloat() * 0.4F + 0.8F,
				entity -> 0.25f);

		private SoundConfiguration<Level> ambientSound = new SoundConfiguration<>(
				BuiltInRegistries.SOUND_EVENT.getKey(SoundEvents.PORTAL_AMBIENT),
				level -> 0.5f,
				level -> level.getRandom().nextFloat() * 0.4F + 0.8F);

		public void travelSound(Identifier soundIdentifier, Function<Entity, Float> volume, Function<Entity, Float> pitch) {
			this.travelSound = new SoundConfiguration<>(soundIdentifier, volume, pitch);
		}

		public void triggerSound(Identifier soundIdentifier, Function<Entity, Float> volume, Function<Entity, Float> pitch) {
			this.triggerSound = new SoundConfiguration<>(soundIdentifier, volume, pitch);
		}

		public void ambientSound(Identifier soundIdentifier, Function<Level, Float> volume, Function<Level, Float> pitch) {
			this.ambientSound = new SoundConfiguration<>(soundIdentifier, volume, pitch);
		}

		public PortalSoundManager build() {
			return new PortalSoundManager(this);
		}
	}
}