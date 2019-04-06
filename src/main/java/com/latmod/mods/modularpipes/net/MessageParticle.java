package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.ModularPipes;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageParticle implements IMessage
{
	public int x, y, z, facing, type;

	public MessageParticle()
	{
	}

	public MessageParticle(BlockPos pos, @Nullable EnumFacing f, int t)
	{
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		facing = f == null ? 6 : f.getIndex();
		type = t;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readUnsignedByte();
		z = buf.readInt();
		facing = buf.readUnsignedByte();
		type = buf.readUnsignedByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		buf.writeByte(facing);
		buf.writeByte(type);
	}

	public static class Handler implements IMessageHandler<MessageParticle, IMessage>
	{
		@Override
		public IMessage onMessage(MessageParticle message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> ModularPipes.PROXY.spawnParticle(new BlockPos(message.x, message.y, message.z), message.facing == 6 ? null : EnumFacing.VALUES[message.facing], message.type));
			return null;
		}
	}
}