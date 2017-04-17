package com.latmod.modularpipes.net;

import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.client.ModularPipesClientEventHandler;
import gnu.trove.list.array.TIntArrayList;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
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
        updated = new ArrayList<>(s);
        while(--s >= 0)
        {
            TransportedItem item = new TransportedItem();
            item.readFromByteBuf(buf);
            updated.add(item);
        }

        s = buf.readInt();
        removed = new TIntArrayList(s);
        while(--s >= 0)
        {
            removed.add(buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(updated.size());
        for(TransportedItem item : updated)
        {
            item.writeToByteBuf(buf);
        }

        buf.writeInt(removed.size());
        for(int i = 0; i < removed.size(); i++)
        {
            buf.writeInt(removed.get(i));
        }
    }

    @Override
    public IMessage onMessage(MessageUpdateItems message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> ModularPipesClientEventHandler.updateItems(message.updated, message.removed));
        return null;
    }
}