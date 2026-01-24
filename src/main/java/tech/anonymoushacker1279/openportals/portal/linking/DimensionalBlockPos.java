package tech.anonymoushacker1279.openportals.portal.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

/**
 * Represents a block position in a specific dimension.
 *
 * @param dimension The dimension of the block position.
 * @param pos       The block position.
 */
public record DimensionalBlockPos(
		Identifier dimension,
		BlockPos pos
) {

	public static final Codec<DimensionalBlockPos> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("dimID").forGetter(DimensionalBlockPos::dimension),
					BlockPos.CODEC.fieldOf("pos").forGetter(DimensionalBlockPos::pos)
			).apply(instance, DimensionalBlockPos::new)
	);
}