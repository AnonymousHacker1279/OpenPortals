package tech.anonymoushacker1279.openportals.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import tech.anonymoushacker1279.openportals.OpenPortals;
import tech.anonymoushacker1279.openportals.network.PlayerSoundPayload;
import tech.anonymoushacker1279.openportals.network.PlayerSoundPayloadHandler;

@EventBusSubscriber(modid = OpenPortals.MOD_ID)
public class ModEventSubscriber {

	@SubscribeEvent
	public static void registerPayloadHandlerEvent(RegisterPayloadHandlersEvent event) {
		String version = ModList.get()
				.getModContainerById(OpenPortals.MOD_ID)
				.map(ModContainer::getModInfo)
				.map(IModInfo::getVersion)
				.map(ArtifactVersion::toString)
				.orElse("[UNKNOWN]");

		PayloadRegistrar registrar = event.registrar(version);

		registrar.playToClient(
				PlayerSoundPayload.TYPE,
				PlayerSoundPayload.STREAM_CODEC,
				PlayerSoundPayloadHandler.getInstance()::handleData
		);
	}
}