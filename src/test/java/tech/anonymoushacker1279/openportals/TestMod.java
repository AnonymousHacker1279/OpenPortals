package tech.anonymoushacker1279.openportals;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import tech.anonymoushacker1279.openportals.api.CustomPortalBuilder;

@EventBusSubscriber(modid = OpenPortals.MOD_ID)
public class TestMod {

	@SubscribeEvent
	public static void onCommonStartUp(FMLCommonSetupEvent event) {
		new CustomPortalBuilder()
				.frame(Blocks.GLOWSTONE)
				.destination(Identifier.withDefaultNamespace("the_nether"))
				.lightWithFluid(Fluids.WATER)
				.tintColor(255, 0, 255)
				.build();

		new CustomPortalBuilder()
				.frame(Blocks.DIAMOND_BLOCK)
				.destination(Identifier.withDefaultNamespace("the_end"))
				.flatPortal()
				.lightWithItem(Items.DIAMOND)
				.tintColor(0, 255, 255)
				.preTeleportEvent(entity -> entity.getWeaponItem() != null && !entity.getWeaponItem().is(Items.NETHERITE_BLOCK))
				.postTeleportEvent(entity -> OpenPortals.LOGGER.info("Teleported entity: {}", entity.getName().getString()))
				.prePortalIgniteEvent((pos, source) -> source.player != null && !source.player.getInventory().contains(ItemTags.AXES))
				.postPortalIgniteEvent((pos, source) -> OpenPortals.LOGGER.info("Portal ignited at position: {}", pos))
				.travelSound(
						SoundEvents.LIGHTNING_BOLT_THUNDER.location(),
						(entity) -> entity.getRandom().nextFloat() * 0.4F + 0.8F,
						(entity) -> 1.0f
				)
				.triggerSound(SoundEvents.WATER_AMBIENT.location(), (entity) -> entity.getRandom().nextFloat() * 0.4F + 0.8F, (entity) -> 1.0f)
				.ambientSound(SoundEvents.AMETHYST_BLOCK_CHIME.location(), (level -> 1.0f), (level) -> level.random.nextFloat() * 0.4F + 0.8F)
				.portalParticle((level, pos) -> ParticleTypes.CRIT)
				.build();
	}
}