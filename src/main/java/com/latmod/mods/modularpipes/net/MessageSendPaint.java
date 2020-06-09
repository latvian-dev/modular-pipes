package com.latmod.mods.modularpipes.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author LatvianModder
 */
public class MessageSendPaint
{
	public int paint;

	public MessageSendPaint()
	{
	}

	public MessageSendPaint(PacketBuffer p)
	{
		fromBytes(p);
	}

	public MessageSendPaint(int p)
	{
		paint = p;
	}

	public void onMessage(NetworkEvent.Context ctx)
	{
		ServerPlayerEntity player = ctx.getSender();

		//		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
		//			if (player.openContainer instanceof ContainerPainter)
		//			{
		//				ItemPainter.setPaint(((ContainerPainter) player.openContainer).stack, message.paint);
		//			}
		//		});
	}

	public void fromBytes(PacketBuffer buf)
	{
		paint = buf.readInt();
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeInt(paint);
	}

}