package tech.anonymoushacker1279.openportals.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import tech.anonymoushacker1279.openportals.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalIgnitionSource;
import tech.anonymoushacker1279.openportals.portal.frame.FlatPortalFrameTester;
import tech.anonymoushacker1279.openportals.portal.frame.PortalFrameTester;
import tech.anonymoushacker1279.openportals.util.PortalLink;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CustomPortalBuilder {

	private final PortalLink portalLink;

	public CustomPortalBuilder() {
		portalLink = new PortalLink();
	}

	/**
	 * Register the portal when completed. This should be called last, only when you are finished configuring the
	 * portal.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public void build() {
		OpenPortals.getPortalManager().addPortal(portalLink.getFrameBlock(), portalLink);
	}

	/**
	 * Specify a frame block as a {@link Identifier}.
	 *
	 * @param identifier Identifier of the Block to be used as the portal's frame block
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder frame(Identifier identifier) {
		portalLink.setFrameBlock(BuiltInRegistries.BLOCK.getValue(identifier));
		return this;
	}

	/**
	 * Specify a frame block as a {@link Block}.
	 *
	 * @param block Block to be used as the portal's frame block
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder frame(Block block) {
		portalLink.setFrameBlock(block);
		return this;
	}

	/**
	 * Specify the destination dimension of the portal.
	 *
	 * @param identifier Identifier of the dimension the portal will teleport to
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder destination(Identifier identifier) {
		portalLink.targetDimensionIdentifier = identifier;
		return this;
	}

	/**
	 * Specify the color to be used to tint the portal block. Accepts a single int value.
	 *
	 * @param color Color to be used to tint the portal block
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder tintColor(int color) {
		portalLink.color = color;
		return this;
	}

	/**
	 * Specify the color in RGB to be used to tint the portal block.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder tintColor(int r, int g, int b) {
		portalLink.color = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
		return this;
	}

	/**
	 * Set the ignition source to an item.
	 *
	 * @param item Item to be used to ignite the portal
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder lightWithItem(Item item) {
		portalLink.ignitionSource = PortalIgnitionSource.fromItem(item);
		return this;
	}

	/**
	 * Set the ignition source to a fluid.
	 *
	 * @param fluid Fluid to be used to ignite the portal
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder lightWithFluid(Fluid fluid) {
		portalLink.ignitionSource = PortalIgnitionSource.fromFluid(fluid);
		return this;
	}

	/**
	 * Specify a custom ignition source to ignite the portal. You must manually trigger the ignition yourself.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder customIgnitionSource(Identifier customSourceLocation) {
		portalLink.ignitionSource = PortalIgnitionSource.fromCustomSource(customSourceLocation);
		return this;
	}

	/**
	 * Specify a custom ignition source to ignite the portal. You must manually trigger the ignition yourself.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder customIgnitionSource(PortalIgnitionSource ignitionSource) {
		portalLink.ignitionSource = ignitionSource;
		return this;
	}

	/**
	 * Set specific dimensions for the portal.
	 *
	 * @param width  Width of portal
	 * @param height Height of portal
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder withStrictDimensions(int width, int height) {
		portalLink.strictWidth = width;
		portalLink.strictHeight = height;
		return this;
	}

	/**
	 * Specify a custom block to be used as the portal block.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder customPortalBlock(CustomPortalBlock portalBlock) {
		portalLink.portalBlock = portalBlock;
		return this;
	}

	/**
	 * Specify the dimension this portal will return you to.
	 *
	 * @param returnDimensionLocation        Identifier of the dimension the portal will return you to
	 * @param onlyIgnitableInReturnDimension Whether the portal can only be ignited in the return dimension
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder returnDimension(Identifier returnDimensionLocation, boolean onlyIgnitableInReturnDimension) {
		portalLink.returnDimensionIdentifier = returnDimensionLocation;
		portalLink.onlyIgnitableInReturnDimension = onlyIgnitableInReturnDimension;
		return this;
	}

	/**
	 * Specify that this portal can only be ignited in the overworld. Attempting to light it in other dimensions will
	 * fail.
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder onlyLightInOverworld() {
		portalLink.onlyIgnitableInReturnDimension = true;
		return this;
	}

	/**
	 * Specify that this is a flat portal (end portal style).
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder flatPortal() {
		portalLink.portalFrameTester = new FlatPortalFrameTester();
		return this;
	}

	/**
	 * Specify a custom portal frame tester to be used.
	 *
	 * @param frameTester The custom portal frame tester to be used
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder customFrameTester(PortalFrameTester frameTester) {
		portalLink.portalFrameTester = frameTester;
		return this;
	}

	/**
	 * Register an event to be called immediately before the specified entity is teleported. Returning true will allow
	 * the teleportation to continue, while returning false will cancel it.
	 *
	 * @param event A function that accepts an entity and returns a boolean
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder preTeleportEvent(Function<Entity, Boolean> event) {
		portalLink.setPreTeleportEvent(event);
		return this;
	}

	/**
	 * Register an event to be called after the specified entity is teleported.
	 *
	 * @param event A consumer that accepts an entity
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder postTeleportEvent(Consumer<Entity> event) {
		portalLink.setPostTeleportEvent(event);
		return this;
	}

	/**
	 * Register an event to be called immediately before the portal is ignited. Returning false will prevent the portal
	 * from igniting.
	 *
	 * @param event A function that accepts a BlockPos and PortalIgnitionSource, returning a boolean
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder prePortalIgniteEvent(BiFunction<BlockPos, PortalIgnitionSource, Boolean> event) {
		portalLink.setPrePortalIgniteEvent(event);
		return this;
	}

	/**
	 * Register an event to be called after the portal is ignited.
	 *
	 * @param event A consumer that accepts a BlockPos and PortalIgnitionSource
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder postPortalIgniteEvent(BiConsumer<BlockPos, PortalIgnitionSource> event) {
		portalLink.setPostPortalIgniteEvent(event);
		return this;
	}

	/**
	 * Set the sound to be played when the player travels through the portal. Volume and pitch are accepted as functions
	 * to allow for dynamic values.
	 *
	 * @param travelSoundLocation Identifier of the sound to be played
	 * @param travelSoundVolume   Volume of the sound
	 * @param travelSoundPitch    Pitch of the sound
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder travelSound(Identifier travelSoundLocation, Function<Entity, Float> travelSoundVolume, Function<Entity, Float> travelSoundPitch) {
		portalLink.setTravelSound(travelSoundLocation, travelSoundVolume, travelSoundPitch);
		return this;
	}

	/**
	 * Set the sound to be played when the player travels through the portal. Volume and pitch are accepted as functions
	 * to allow for dynamic values.
	 *
	 * @param triggerSoundLocation Identifier of the sound to be played
	 * @param triggerSoundVolume   Volume of the sound
	 * @param triggerSoundPitch    Pitch of the sound
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder triggerSound(Identifier triggerSoundLocation, Function<Entity, Float> triggerSoundVolume, Function<Entity, Float> triggerSoundPitch) {
		portalLink.setTriggerSound(triggerSoundLocation, triggerSoundVolume, triggerSoundPitch);
		return this;
	}

	/**
	 * Set the sound to be played randomly while nearby the portal.
	 *
	 * @param ambientSoundLocation Identifier of the sound to be played
	 * @param ambientSoundVolume   Volume of the sound
	 * @param ambientSoundPitch    Pitch of the sound
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder ambientSound(Identifier ambientSoundLocation, Function<Level, Float> ambientSoundVolume, Function<Level, Float> ambientSoundPitch) {
		portalLink.setAmbientSound(ambientSoundLocation, ambientSoundVolume, ambientSoundPitch);
		return this;
	}

	/**
	 * Set a custom portal particle effect.
	 *
	 * @param particleFunction A function that accepts a Level and BlockPos and returns a ParticleOptions
	 */
	@ApiStatus.AvailableSince("1.0.0")
	public CustomPortalBuilder portalParticle(BiFunction<Level, BlockPos, ParticleOptions> particleFunction) {
		portalLink.portalParticle = particleFunction;
		return this;
	}
}