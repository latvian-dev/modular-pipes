package dev.latvian.mods.modularpipes.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ParticleMessage extends BaseS2CMessage {
	public final int x, y, z, facing, type;

	public ParticleMessage(BlockPos pos, @Nullable Direction f, int t) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		facing = f == null ? 6 : f.ordinal();
		type = t;
	}

	public ParticleMessage(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readUnsignedByte();
		z = buf.readInt();
		facing = buf.readUnsignedByte();
		type = buf.readUnsignedByte();
	}

	@Override
	public MessageType getType() {
		return ModularPipesNet.PARTICLE;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		buf.writeByte(facing);
		buf.writeByte(type);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ModularPipes.PROXY.spawnParticle(new BlockPos(x, y, z), facing == 6 ? null : Direction.values()[facing], type);
	}
}