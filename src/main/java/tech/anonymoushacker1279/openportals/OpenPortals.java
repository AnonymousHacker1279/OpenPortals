package tech.anonymoushacker1279.openportals;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;
import tech.anonymoushacker1279.openportals.portal.PortalManager;

import java.util.function.Supplier;

@Mod(OpenPortals.MOD_ID)
public class OpenPortals {

	public static final String MOD_ID = "openportals";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
	public static final Supplier<CustomPortalBlock> CUSTOM_PORTAL_BLOCK = BLOCKS.registerBlock("custom_portal_block",
			(properties) -> new CustomPortalBlock(properties
					.noCollision()
					.randomTicks()
					.strength(-1)
					.sound(SoundType.GLASS)
					.lightLevel(state -> 11)));

	private static final PortalManager manager = new PortalManager();

	public OpenPortals(IEventBus bus) {
		BLOCKS.register(bus);
	}

	public static PortalManager getPortalManager() {
		return manager;
	}
}