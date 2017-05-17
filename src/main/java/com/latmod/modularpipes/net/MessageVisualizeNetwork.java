package com.latmod.modularpipes.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.latmod.modularpipes.data.NodeType;
import com.latmod.modularpipes.data.PipeNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MessageVisualizeNetwork extends MessageToClient<MessageVisualizeNetwork>
{
    private Map<BlockPos, NodeType> nodes;
    private Collection<List<BlockPos>> links;
    private Collection<BlockPos> tiles;

    public MessageVisualizeNetwork()
    {
    }

    public MessageVisualizeNetwork(Map<BlockPos, NodeType> n, Collection<List<BlockPos>> l, Collection<BlockPos> t)
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
        int s1 = buf.readInt();
        nodes = new HashMap<>(s + s1);
        while(--s >= 0)
        {
            nodes.put(NetUtils.readPos(buf), NodeType.SIMPLE);
        }
        while(--s1 >= 0)
        {
            nodes.put(NetUtils.readPos(buf), NodeType.TILES);
        }
        s = buf.readInt();
        links = new ArrayList<>(s);
        while(--s >= 0)
        {
            s1 = buf.readUnsignedByte();
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
        List<BlockPos> nodesSimple = new ArrayList<>();
        List<BlockPos> nodesTiles = new ArrayList<>();

        for(Map.Entry<BlockPos, NodeType> entry : nodes.entrySet())
        {
            if(entry.getValue().hasTiles())
            {
                nodesTiles.add(entry.getKey());
            }
            else
            {
                nodesSimple.add(entry.getKey());
            }
        }

        buf.writeInt(nodesSimple.size());
        buf.writeInt(nodesTiles.size());
        for(BlockPos pos : nodesSimple)
        {
            NetUtils.writePos(buf, pos);
        }
        for(BlockPos pos : nodesTiles)
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
        PipeNetwork.get(player.world).visualizeNetwork(message.nodes, message.links, message.tiles);
    }
}