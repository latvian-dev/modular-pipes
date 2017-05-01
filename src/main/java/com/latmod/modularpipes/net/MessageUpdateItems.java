package com.latmod.modularpipes.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.latmod.modularpipes.client.ModularPipesClientEventHandler;
import com.latmod.modularpipes.data.TransportedItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MessageUpdateItems extends MessageToClient<MessageUpdateItems>
{
    private Map<Integer, TransportedItem> updated;

    public MessageUpdateItems()
    {
    }

    public MessageUpdateItems(Map<Integer, TransportedItem> u)
    {
        updated = u;
    }

    @Override
    public NetworkWrapper getWrapper()
    {
        return ModularPipesNet.NET;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int s = buf.readInt();
        updated = new HashMap<>(s);
        while(--s >= 0)
        {
            TransportedItem item = new TransportedItem();
            item.readFromByteBuf(buf);

            if(!item.action.remove())
            {
                updated.put(item.id, item);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(updated.size());
        for(TransportedItem item : updated.values())
        {
            item.writeToByteBuf(buf);
        }
    }

    @Override
    public void onMessage(MessageUpdateItems message, EntityPlayer player)
    {
        message.updated.forEach(ModularPipesClientEventHandler.FOREACH_UPDATE_ITEMS);
    }
}