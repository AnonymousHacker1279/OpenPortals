package net.kyrptonaught.customportalapi.event;

import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.mixin.client.ChunkRendererRegionAccessor;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.kyrptonaught.customportalapi.util.PortalLink;

@EventBusSubscriber(modid = CustomPortalsMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {

    @SubscribeEvent
    public static void registerBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
        event.register((state, tintGetter, pos, tintIndex) -> {
            if (pos != null && tintGetter instanceof RenderSectionRegion) {
                Block block = CustomPortalHelper.getPortalBase(((ChunkRendererRegionAccessor) tintGetter).getLevel(), pos);
                PortalLink link = CustomPortalsMod.getPortalLinkFromBase(block);
                if (link != null) {
                    return link.color;
                }
            }
            return 1908001;
        }, CustomPortalsMod.CUSTOM_PORTAL_BLOCK.get());
    }
}
