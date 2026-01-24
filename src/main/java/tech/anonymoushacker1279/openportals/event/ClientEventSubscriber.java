package tech.anonymoushacker1279.openportals.event;

import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.mixin.client.ChunkRendererRegionAccessor;
import tech.anonymoushacker1279.openportals.util.CustomPortalHelper;
import tech.anonymoushacker1279.openportals.util.PortalLink;

@EventBusSubscriber(modid = OpenPortals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {

	@SubscribeEvent
	public static void registerBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
		event.register((state, tintGetter, pos, tintIndex) -> {
			if (pos != null && tintGetter instanceof RenderSectionRegion) {
				Block block = CustomPortalHelper.getPortalBase(((ChunkRendererRegionAccessor) tintGetter).getLevel(), pos);
				PortalLink link = OpenPortals.getPortalLinkFromBase(block);
				if (link != null) {
					return link.color;
				}
			}
			return 1908001;
		}, OpenPortals.CUSTOM_PORTAL_BLOCK.get());
	}
}