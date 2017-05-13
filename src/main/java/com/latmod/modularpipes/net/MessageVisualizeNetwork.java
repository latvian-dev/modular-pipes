package com.latmod.modularpipes.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.latmod.modularpipes.client.ClientPipeNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageVisualizeNetwork extends MessageToClient<MessageVisualizeNetwork>
{
    private Collection<BlockPos> nodes;
    private Collection<List<BlockPos>> links;
    private Collection<BlockPos> tiles;

    public MessageVisualizeNetwork()
    {
    }

    public MessageVisualizeNetwork(Collection<BlockPos> n, Collection<List<BlockPos>> l, Collection<BlockPos> t)
    {
        nodes = n;
        links = l;
        tiles = t;
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
        nodes = new ArrayList<>(s);
        while(--s >= 0)
        {
            nodes.add(NetUtils.readPos(buf));
        }
        s = buf.readInt();
        links = new ArrayList<>(s);
        while(--s >= 0)
        {
            int s1 = buf.readUnsignedByte();
            List<BlockPos> l = new ArrayList<>();

            while(--s1 >= 0)
            {
                l.add(NetUtils.readPos(buf));
            }
            links.add(l);
        }
        s = buf.readInt();
        tiles = new ArrayList<>(s);
        while(--s >= 0)
        {
            tiles.add(NetUtils.readPos(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(nodes.size());
        for(BlockPos pos : nodes)
        {
            NetUtils.writePos(buf, pos);
        }
        buf.writeInt(links.size());
        for(List<BlockPos> l : links)
        {
            buf.writeByte(l.size());
            for(BlockPos pos : l)
            {
                NetUtils.writePos(buf, pos);
            }
        }
        buf.writeInt(tiles.size());
        for(BlockPos pos : tiles)
        {
            NetUtils.writePos(buf, pos);
        }
    }

    @Override
    public void onMessage(MessageVisualizeNetwork message, EntityPlayer player)
    {
        ClientPipeNetwork.get().visualizeNetwork(message.nodes, message.links, message.tiles);
    }
}