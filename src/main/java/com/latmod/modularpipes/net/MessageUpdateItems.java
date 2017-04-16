package com.latmod.modularpipes.net;

import com.latmod.modularpipes.api.TransportedItem;
import gnu.trove.list.array.TIntArrayList;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageUpdateItems implements IMessage, IMessageHandler<MessageUpdateItems, IMessage>
{
    public List<TransportedItem> updated;
    public TIntArrayList removed;

    public MessageUpdateItems()
    {
    }

    public MessageUpdateItems(List<TransportedItem> u, TIntArrayList r)
    {
        updated = u;
        removed = r;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int s = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    @Override
    public IMessage onMessage(MessageUpdateItems message, MessageContext ctx)
    {
        return null;
    }
}