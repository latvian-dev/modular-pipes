package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.item.ItemPainter;
import com.latmod.mods.modularpipes.item.ModularPipesItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class MessageSendPaint implements IMessage
{
	public ItemStack stack;

	public MessageSendPaint()
	{
	}

	public MessageSendPaint(ItemStack is)
	{
		stack = is;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler implements IMessageHandler<MessageSendPaint, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSendPaint message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

				if (stack.getItem() == ModularPipesItems.PAINTER)
				{
					ItemPainter.setPaint(stack, message.stack);
				}
			});

			return null;
		}
	}

}