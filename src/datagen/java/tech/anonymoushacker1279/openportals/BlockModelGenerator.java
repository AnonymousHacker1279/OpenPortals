package tech.anonymoushacker1279.openportals;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tech.anonymoushacker1279.openportals.portal.CustomPortalBlock;

public class BlockModelGenerator extends ModelProvider {

	public BlockModelGenerator(PackOutput output) {
		super(output, OpenPortals.MOD_ID);
	}

	private static void generatePortal(BlockModelGenerators models, Block block) {
		TextureMapping mapping = TextureMapping.defaultTexture(OpenPortals.CUSTOM_PORTAL_BLOCK.get())
				.put(TextureSlot.PARTICLE, new Material(ModelLocationUtils.getModelLocation(OpenPortals.CUSTOM_PORTAL_BLOCK.get())));

		ExtendedModelTemplate templateEW = ExtendedModelTemplateBuilder.builder()
				.suffix("_ew")
				.requiredTextureSlot(TextureSlot.TEXTURE)
				.requiredTextureSlot(TextureSlot.PARTICLE)
				.element(builder -> builder
						.from(6, 0, 0)
						.to(10, 16, 16)
						.texture(TextureSlot.TEXTURE)
						.face(Direction.EAST, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0))
						.face(Direction.WEST, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0)))
				.build();

		ExtendedModelTemplate templateNS = ExtendedModelTemplateBuilder.builder()
				.suffix("_ns")
				.requiredTextureSlot(TextureSlot.TEXTURE)
				.requiredTextureSlot(TextureSlot.PARTICLE)
				.element(builder -> builder
						.from(0, 0, 6)
						.to(16, 16, 10)
						.texture(TextureSlot.TEXTURE)
						.face(Direction.NORTH, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0))
						.face(Direction.SOUTH, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0)))
				.build();

		ExtendedModelTemplate templateUD = ExtendedModelTemplateBuilder.builder()
				.suffix("_ud")
				.requiredTextureSlot(TextureSlot.TEXTURE)
				.requiredTextureSlot(TextureSlot.PARTICLE)
				.element(builder -> builder
						.from(0, 6, 0)
						.to(16, 10, 16)
						.texture(TextureSlot.TEXTURE)
						.face(Direction.UP, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0))
						.face(Direction.DOWN, faceBuilder -> faceBuilder
								.texture(TextureSlot.TEXTURE)
								.tintindex(0)))
				.build();

		models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(CustomPortalBlock.AXIS)
						.select(Direction.Axis.X, BlockModelGenerators.plainVariant(templateNS.create(block, mapping, models.modelOutput)))
						.select(Direction.Axis.Z, BlockModelGenerators.plainVariant(templateEW.create(block, mapping, models.modelOutput)))
						.select(Direction.Axis.Y, BlockModelGenerators.plainVariant(templateUD.create(block, mapping, models.modelOutput)))
				)
		);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		generatePortal(blockModels, OpenPortals.CUSTOM_PORTAL_BLOCK.get());
	}
}