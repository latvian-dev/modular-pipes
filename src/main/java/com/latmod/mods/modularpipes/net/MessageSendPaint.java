package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.gui.painter.ContainerPainter;
import com.latmod.mods.modularpipes.item.ItemPainter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class MessageSendPaint implements IMessage
{
	public int paint;

	public MessageSendPaint()
	{
	}

	public MessageSendPaint(int p)
	{
		paint = p;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		paint = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(paint);
	}

	public static class Handler implements IMessageHandler<MessageSendPaint, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSendPaint message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (player.openContainer instanceof ContainerPainter)
				{
					ItemPainter.setPaint(((ContainerPainter) player.openContainer).stack, message.paint);
				}
			});

			return null;
		}
	}

}