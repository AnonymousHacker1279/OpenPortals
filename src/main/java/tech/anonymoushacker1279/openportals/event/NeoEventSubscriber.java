package tech.anonymoushacker1279.openportals.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalIgniter;
import tech.anonymoushacker1279.openportals.portal.PortalIgnitionSource;
import tech.anonymoushacker1279.openportals.portal.linking.PortalLinkingStorage;

@EventBusSubscriber(modid = OpenPortals.MOD_ID)
public class NeoEventSubscriber {

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent event) {
		OpenPortals.getPortalManager().populateDimensions(event.getServer().levelKeys());
		OpenPortals.getPortalManager().setStorage(event.getServer().overworld()
				.getDataStorage()
				.computeIfAbsent(PortalLinkingStorage.TYPE));
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		// Clean up orphaned portal links before shutdown
		PortalLinkingStorage storage = OpenPortals.getPortalManager().getStorage();
		if (storage != null) {
			int removed = storage.cleanupOrphanedLinks(event.getServer());
			if (removed > 0) {
				OpenPortals.LOGGER.info("Cleaned up {} orphaned portal link(s) during shutdown", removed);
			}
		}
	}

	@SubscribeEvent
	public static void rightClickItemEvent(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();
		Level level = event.getLevel();
		InteractionHand hand = event.getHand();
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			Item item = stack.getItem();
			if (PortalIgnitionSource.isRegisteredIgnitionSourceWith(item)) {
				HitResult hit = player.pick(6, 1, false);
				if (hit.getType() == HitResult.Type.BLOCK) {
					BlockHitResult blockHit = (BlockHitResult) hit;
					if (PortalIgniter.attemptPortalLight(level,
							blockHit.getBlockPos().relative(blockHit.getDirection()),
							PortalIgnitionSource.fromItem(item).withPlayer(player))) {

						event.setCanceled(true);
					}
				}
			}
		}
	}
}