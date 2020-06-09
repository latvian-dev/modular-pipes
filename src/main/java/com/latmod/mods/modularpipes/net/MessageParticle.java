package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.ModularPipes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageParticle
{
	public int x, y, z, facing, type;

	public MessageParticle()
	{
	}

	public MessageParticle(BlockPos pos, @Nullable Direction f, int t)
	{
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		facing = f == null ? 6 : f.getIndex();
		type = t;
	}

	public MessageParticle(PacketBuffer p)
	{
		fromBytes(p);
	}

	public void onMessage(NetworkEvent.Context ctx)
	{
		ctx.enqueueWork(() -> ModularPipes.PROXY.spawnParticle(new BlockPos(x, y, z), facing == 6 ? null : Direction.values()[facing], type));
	}

	public void fromBytes(PacketBuffer buf)
	{
		x = buf.readInt();
		y = buf.readUnsignedByte();
		z = buf.readInt();
		facing = buf.readUnsignedByte();
		type = buf.readUnsignedByte();
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		buf.writeByte(facing);
		buf.writeByte(type);
	}
}