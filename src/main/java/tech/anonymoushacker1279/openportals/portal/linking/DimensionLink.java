package tech.anonymoushacker1279.openportals.portal.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a bidirectional link between two portal positions. The link works in both directions - you can travel from
 * pos1 to pos2, or from pos2 to pos1.
 *
 * @param pos1 The first portal position.
 * @param pos2 The second portal position.
 */
public record DimensionLink(DimensionalBlockPos pos1, DimensionalBlockPos pos2) {

	public static final Codec<DimensionLink> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					DimensionalBlockPos.CODEC.fieldOf("pos1").forGetter(DimensionLink::pos1),
					DimensionalBlockPos.CODEC.fieldOf("pos2").forGetter(DimensionLink::pos2)
			).apply(instance, DimensionLink::new)
	);

	/**
	 * Check if this link connects the given position.
	 *
	 * @param position the position to check
	 * @return true if this link connects to the given position
	 */
	public boolean connects(DimensionalBlockPos position) {
		return (pos1.dimension().equals(position.dimension()) && pos1.pos().equals(position.pos()))
				|| (pos2.dimension().equals(position.dimension()) && pos2.pos().equals(position.pos()));
	}

	/**
	 * Get the destination for the given source position. If the source is pos1, returns pos2. If the source is pos2,
	 * returns pos1.
	 *
	 * @param source the source position
	 * @return the destination position, or null if source doesn't match either end
	 */
	@Nullable
	public DimensionalBlockPos getDestination(DimensionalBlockPos source) {
		if (pos1.dimension().equals(source.dimension()) && pos1.pos().equals(source.pos())) {
			return pos2;
		} else if (pos2.dimension().equals(source.dimension()) && pos2.pos().equals(source.pos())) {
			return pos1;
		}
		return null;
	}
}