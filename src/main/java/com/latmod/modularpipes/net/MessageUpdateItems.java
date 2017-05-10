package com.latmod.modularpipes.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.latmod.modularpipes.client.ClientPipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
            TransportedItem item = new TransportedItem(null);
            item.id = buf.readInt();
            item.action = TransportedItem.Action.VALUES[buf.readUnsignedByte()];

            if(!item.action.remove())
            {
                item.stack = ByteBufUtils.readItemStack(buf);
                item.path.clear();
                int s1 = buf.readUnsignedShort();

                while(--s1 >= 0)
                {
                    item.path.add(NetUtils.readPos(buf));
                }

                item.filters = buf.readUnsignedShort();
                item.speed = buf.readFloat();
                item.progress = buf.readFloat();
            }

            updated.put(item.id, item);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(updated.size());
        for(TransportedItem item : updated.values())
        {
            buf.writeInt(item.id);
            buf.writeByte(item.action.ordinal());

            if(!item.action.remove())
            {
                ByteBufUtils.writeItemStack(buf, item.stack);
                buf.writeShort(item.path.size());

                for(BlockPos p : item.path)
                {
                    NetUtils.writePos(buf, p);
                }

                buf.writeShort(item.filters);
                buf.writeFloat(item.speed);
                buf.writeFloat(item.progress);
            }
        }
    }

    @Override
    public void onMessage(MessageUpdateItems message, EntityPlayer player)
    {
        message.updated.forEach(ClientPipeNetwork.get().foreachUpdateItems);
    }
}