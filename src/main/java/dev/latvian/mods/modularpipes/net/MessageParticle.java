package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageParticle {
	public final int x, y, z, facing, type;

	public MessageParticle(BlockPos pos, @Nullable Direction f, int t) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		facing = f == null ? 6 : f.ordinal();
		type = t;
	}

	public MessageParticle(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readUnsignedByte();
		z = buf.readInt();
		facing = buf.readUnsignedByte();
		type = buf.readUnsignedByte();
	}

	public void onMessage(NetworkEvent.Context ctx) {
		ctx.enqueueWork(() -> ModularPipes.PROXY.spawnParticle(new BlockPos(x, y, z), facing == 6 ? null : Direction.values()[facing], type));
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		buf.writeByte(facing);
		buf.writeByte(type);
	}
}