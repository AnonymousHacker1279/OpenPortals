package tech.anonymoushacker1279.openportals.event;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.portal.PortalLink;
import tech.anonymoushacker1279.openportals.util.PortalUtils;

import java.util.List;

@EventBusSubscriber(modid = OpenPortals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {

	@SubscribeEvent
	public static void registerBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
		event.register(List.of(new PortalTintSource()), OpenPortals.CUSTOM_PORTAL_BLOCK.get());
	}

	static class PortalTintSource implements BlockTintSource {
		@Override
		public int color(BlockState state) {
			return ARGB.color(255, 255, 255, 255);
		}

		@Override
		public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
			if (level instanceof RenderSectionRegion region) {
				Block base = PortalUtils.getPortalBase(region.level, pos);
				PortalLink link = OpenPortals.getPortalManager().getPortalLinkFromBase(base);
				if (link != null) {
					return link.getColor();
				}
			}

			return ARGB.color(255, 255, 255, 255);
		}
	}
}