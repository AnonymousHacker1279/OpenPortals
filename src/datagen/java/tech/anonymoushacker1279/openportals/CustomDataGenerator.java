package tech.anonymoushacker1279.openportals;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class CustomDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();

		generator.addProvider(true, new BlockModelGenerator(output));
		generator.addProvider(true, new LanguageGenerator(output));
		generator.addProvider(true, PackMetadataGenerator.forFeaturePack(output, Component.literal("OpenPortals Resources")));
	}
}