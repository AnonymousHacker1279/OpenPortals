package tech.anonymoushacker1279.openportals;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LanguageGenerator extends LanguageProvider {

	public LanguageGenerator(PackOutput output) {
		super(output, OpenPortals.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addBlock(OpenPortals.CUSTOM_PORTAL_BLOCK, "Portal");
	}
}