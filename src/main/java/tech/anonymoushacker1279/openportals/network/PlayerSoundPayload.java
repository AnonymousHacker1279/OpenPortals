package tech.anonymoushacker1279.openportals.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import tech.anonymoushacker1279.openportals.OpenPortals;

public record PlayerSoundPayload(Identifier location, float volume, float pitch) implements CustomPacketPayload {

	public static final Type<PlayerSoundPayload> TYPE = new Type<>(
			Identifier.fromNamespaceAndPath(OpenPortals.MOD_ID, "local_sound")
	);

	public static final StreamCodec<FriendlyByteBuf, PlayerSoundPayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			PlayerSoundPayload::location,
			ByteBufCodecs.FLOAT,
			PlayerSoundPayload::volume,
			ByteBufCodecs.FLOAT,
			PlayerSoundPayload::pitch,
			PlayerSoundPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}