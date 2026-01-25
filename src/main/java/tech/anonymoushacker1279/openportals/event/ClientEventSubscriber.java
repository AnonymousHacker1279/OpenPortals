package tech.anonymoushacker1279.openportals.event;

import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalLink;
import tech.anonymoushacker1279.openportals.util.PortalUtils;

@EventBusSubscriber(modid = OpenPortals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {

	@SubscribeEvent
	public static void registerBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
		event.register((state, tintGetter, pos, tintIndex) -> {
			if (pos != null && tintGetter instanceof RenderSectionRegion region) {
				Block block = getPortalBase(region.level, pos);
				PortalLink link = OpenPortals.getPortalManager().getPortalLinkFromBase(block);
				if (link != null) {
					return link.getColor();
				}
			}

			return 1908001;
		}, OpenPortals.CUSTOM_PORTAL_BLOCK.get());
	}

	private static Block getPortalBase(Level level, BlockPos pos) {
		if (level.getBlockState(pos).getBlock() instanceof CustomPortalBlock) {
			return PortalUtils.getPortalBase(level, pos);
		} else if (PortalUtils.isInstanceOfPortalFrame(level, pos)) {
			return level.getBlockState(pos).getBlock();
		}

		return Blocks.AIR;
	}
}