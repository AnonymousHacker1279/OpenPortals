package tech.anonymoushacker1279.openportals;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import tech.anonymoushacker1279.openportals.portal.linking.PortalLinkingStorage;
import tech.anonymoushacker1279.openportals.util.PortalLink;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Mod(OpenPortals.MOD_ID)
public class OpenPortals {

	public static final String MOD_ID = "openportals";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);

	public static final Supplier<CustomPortalBlock> CUSTOM_PORTAL_BLOCK = BLOCKS.registerBlock(
			"custom_portal_block",
			(properties) -> new CustomPortalBlock(
					properties.noCollision()
							.randomTicks()
							.strength(-1)
							.sound(
									SoundType.GLASS
							)
							.lightLevel(state -> 11)
			)
	);

	public static final HashMap<Identifier, ResourceKey<Level>> dimensions = new HashMap<>();

	public static final ConcurrentHashMap<Block, PortalLink> PORTALS = new ConcurrentHashMap<>();

	@Nullable
	public static PortalLinkingStorage portalLinkingStorage;

	public OpenPortals(IEventBus bus) {
		BLOCKS.register(bus);
	}

	@Nullable
	public static PortalLink getPortalLinkFromBase(Block baseBlock) {
		if (PORTALS.containsKey(baseBlock)) {
			return PORTALS.get(baseBlock);
		}

		return null;
	}

	public static boolean isRegisteredFrameBlock(BlockState blockState) {
		return PORTALS.containsKey(blockState.getBlock());
	}

	public static void addPortal(Block frameBlock, PortalLink link) {
		if (!dimensions.isEmpty() && !dimensions.containsKey(link.targetDimensionLocation)) {
			throw new RuntimeException("Dimension not found");
		}

		if (PORTALS.containsKey(frameBlock) || frameBlock.equals(Blocks.OBSIDIAN)) {
			throw new RuntimeException("A portal using the '%s' frame is already registered!'".formatted(frameBlock.getName().getString()));
		} else {
			PORTALS.put(frameBlock, link);
		}
	}
}