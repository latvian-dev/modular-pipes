package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.util.NBTUtils;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.net.MessageUpdateItems;
import com.latmod.modularpipes.net.MessageVisualizeNetwork;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerPipeNetwork extends PipeNetwork
{
    public static final TIntObjectHashMap<PipeNetwork> NETWORK_MAP = new TIntObjectHashMap<>();

    public static void clearAll()
    {
        NETWORK_MAP.clear();
    }

    public boolean loaded;
    private final Map<BlockPos, Node> nodes = new HashMap<>();
    private final Collection<Link> links = new HashSet<>();
    private int nextItemId = 0;
    private final Map<Integer, TransportedItem> updateCache = new HashMap<>();

    ServerPipeNetwork(World w)
    {
        super(w);
    }

    @Override
    public ServerPipeNetwork server()
    {
        return this;
    }

    @Override
    public void clear()
    {
        super.clear();
        nodes.clear();
        links.clear();
        nextItemId = 0;
    }

    public void save()
    {
        try
        {
            File dir = new File(((WorldServer) world).getChunkSaveLocation(), "data/modularpipes");
            File file = new File(dir, "modularpipes.dat");

            if(!file.exists() && nodes.isEmpty() && links.isEmpty() && items.isEmpty())
            {
                return;
            }

            //ModularPipes.LOGGER.info("Saved pipe info to " + dir.getAbsolutePath());
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            for(Node node : getNodes())
            {
                list.appendTag(new NBTTagIntArray(new int[] {node.getX(), node.getY(), node.getZ(), node.type.ordinal()}));
            }

            nbt.setTag("Nodes", list);
            list = new NBTTagList();

            for(Link link : links)
            {
                //link.simplify();
                list.appendTag(link.serializeNBT());
            }
            nbt.setTag("Links", list);
            list = new NBTTagList();

            for(TransportedItem item : items.values())
            {
                list.appendTag(item.serializeNBT());
            }

            nbt.setTag("Items", list);
            NBTUtils.writeTag(file, nbt);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void load()
    {
        clear();
        loaded = true;
        File dir = new File(((WorldServer) world).getChunkSaveLocation(), "data/modularpipes");
        // ModularPipes.LOGGER.info("Loading pipe info from " + dir.getAbsolutePath());

        File file = new File(dir, "modularpipes.dat");
        NBTTagCompound nbt = NBTUtils.readTag(file);

        if(nbt == null)
        {
            return;
        }

        NBTTagList list = nbt.getTagList("Nodes", Constants.NBT.TAG_INT_ARRAY);

        for(int i = 0; i < list.tagCount(); i++)
        {
            int[] pos = list.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                Node node = new Node(this, pos[0], pos[1], pos[2], pos.length >= 4 ? NodeType.VALUES[pos[3]] : NodeType.TILES);
                nodes.put(node, node);
            }
        }

        list = nbt.getTagList("Links", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            Link link = new Link(this, list.getCompoundTagAt(i));

            if(!link.invalid())
            {
                links.add(link);
            }
        }

        list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            TransportedItem item = new TransportedItem(this);
            item.deserializeNBT(list.getCompoundTagAt(i));

            if(!item.remove())
            {
                item.id = ++nextItemId;
                item.action = TransportedItem.Action.UPDATE;
                items.put(item.id, item);
            }
        }
    }

    public void unload()
    {
        loaded = false;

        for(Node node : getNodes())
        {
            node.clearCache();
        }

        NETWORK_MAP.remove(world.provider.getDimension());
    }

    @Override
    @Nullable
    public Node getNode(BlockPos pos)
    {
        return nodes.get(pos);
    }

    @Override
    public Collection<Node> getNodes()
    {
        return nodes.values();
    }

    @Override
    public Collection<Link> getLinks()
    {
        return links;
    }

    @Override
    public boolean removePipe(BlockPos pos, boolean simulate)
    {
        if(!loaded)
        {
            return false;
        }

        Node node = getNode(pos);

        if(simulate)
        {
            if(node != null)
            {
                return true;
            }

            for(Link link : links)
            {
                if(!link.invalid() && link.contains(pos))
                {
                    return true;
                }
            }

            return false;
        }

        boolean removedNode = false, removedLink = false;

        if(node != null)
        {
            removedNode = true;

            if(!node.linkedWith.isEmpty())
            {
                for(Link link : node.linkedWith)
                {
                    link.path.clear();
                }

                removedLink = true;
            }

            nodes.remove(pos);
            networkUpdated = true;
        }

        if(removedLink || node == null)
        {
            Iterator<Link> iterator = links.iterator();

            while(iterator.hasNext())
            {
                Link link = iterator.next();

                if(link.invalid() || (node != null ? link.isEndpoint(pos) : link.contains(pos)))
                {
                    if(link.start != null)
                    {
                        link.start.linkedWith.remove(link);
                    }
                    if(link.end != null)
                    {
                        link.end.linkedWith.remove(link);
                    }

                    iterator.remove();
                    removedLink = true;
                    networkUpdated = true;
                }
            }

        }

        return removedLink || removedNode;
    }

    @Override
    public void addPipe(BlockPos pos, IBlockState state)
    {
        if(loaded && state.getBlock() instanceof IPipeBlock)
        {
            if(addPipe0(pos, ((IPipeBlock) state.getBlock()).getNodeType(world, pos, state), true))
            {
                for(EnumFacing facing : EnumFacing.VALUES)
                {
                    BlockPos pos1 = pos.offset(facing);
                    IBlockState state1 = world.getBlockState(pos1);

                    if(state1.getBlock() instanceof IPipeBlock)
                    {
                        addPipe0(pos1, ((IPipeBlock) state1.getBlock()).getNodeType(world, pos1, state1), true);
                    }
                }
            }
        }
    }

    private boolean addPipe0(BlockPos pos, NodeType type, boolean init)
    {
        if(init && removePipe(pos, true))
        {
            return false;
        }

        boolean isNode = type.isNode();

        if(isNode)
        {
            Node node = getNode(pos);

            if(node == null)
            {
                node = new Node(this, pos.getX(), pos.getY(), pos.getZ(), type);
                nodes.put(node, node);
            }
        }

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            IBlockState state1 = world.getBlockState(pos.offset(facing));

            if(state1.getBlock() instanceof IPipeBlock)
            {
                CachedBlock data = findNode(pos, facing, isNode);

                if(data != null)
                {
                    if(isNode)
                    {
                        Link link = data.getLink();
                        if(link != null && !link.invalid())
                        {
                            link.start.linkedWith.add(link);
                            link.end.linkedWith.add(link);
                            links.add(link);
                        }
                    }
                    else if(data.getNode() != null)
                    {
                        addPipe0(data.getNode(), data.getNode().type, false);
                    }
                }
            }
        }

        networkUpdated = true;
        return true;
    }

    @Nullable
    private CachedBlock findNode(BlockPos start, EnumFacing facing, boolean isNode)
    {
        List<BlockPos> list = new ArrayList<>();
        HashSet<BlockPos> set = new HashSet<>();
        list.add(start);
        set.add(start);
        BlockPos pos = start.offset(facing);
        EnumFacing source = facing.getOpposite();
        IBlockState state1;
        int maxLength = ModularPipesConfig.MAX_LINK_LENGTH.getInt();

        for(int length = 0; length < maxLength; length++)
        {
            state1 = world.getBlockState(pos);

            if(!(state1.getBlock() instanceof IPipeBlock))
            {
                return null;
            }

            IPipeBlock pipe = (IPipeBlock) state1.getBlock();

            if(pipe.getNodeType(world, pos, state1).isNode())
            {
                if(isNode)
                {
                    list.add(pos);
                    return new CachedBlock.LinkData(new Link(this, Link.simplify(list), length));
                }
                else
                {
                    return new CachedBlock.NodeData(new Node(this, pos.getX(), pos.getY(), pos.getZ(), NodeType.SIMPLE));
                }
            }
            else
            {
                EnumFacing facing1 = pipe.getPipeFacing(world, pos, state1, source);

                if(facing1 == source)
                {
                    //ModularPipes.LOGGER.warn("Dead end @ " + pos);
                    return null;
                }
                else
                {
                    source = facing1.getOpposite();
                    pos = pos.offset(facing1);

                    if(set.contains(pos))
                    {
                        //ModularPipes.LOGGER.warn("Loop @ " + pos);
                        return null;
                    }

                    set.add(pos);
                    list.add(pos);
                }
            }
        }

        //ModularPipes.LOGGER.warn("Path too long!");
        return null;
    }

    @Override
    public void addItem(TransportedItem item)
    {
        item.id = ++nextItemId;
        item.progress = 0;

        if(nextItemId == 2000000000)
        {
            nextItemId = 0;
        }

        super.addItem(item);
    }

    @Override
    public void update()
    {
        updateCache.clear();
        super.update();

        if(!updateCache.isEmpty())
        {
            new MessageUpdateItems(updateCache).sendToDimension(world.provider.getDimension());
        }

        if(networkUpdated)
        {
            networkUpdated = false;

            if(ModularPipesConfig.DEV_MODE.getBoolean())
            {
                Map<BlockPos, NodeType> n = new HashMap<>(nodes.size());

                for(Node node : nodes.values())
                {
                    n.put(node, node.type);
                }

                Collection<List<BlockPos>> l = new ArrayList<>();

                for(Link link : links)
                {
                    l.add(link.path);
                }

                Collection<BlockPos> t = new ArrayList<>();

                for(Node node : nodes.values())
                {
                    for(int facing = 0; facing < 6; facing++)
                    {
                        if(node.getTile(facing) != null)
                        {
                            t.add(node.offset(EnumFacing.VALUES[facing]));
                        }
                    }
                }

                new MessageVisualizeNetwork(n, l, t).sendTo(null);
            }
        }
    }

    @Override
    public void itemUpdated(int id, TransportedItem item)
    {
        updateCache.put(item.id, item);
    }

    public void playerLoggedIn(EntityPlayer player)
    {
        new MessageUpdateItems(items).sendTo(player);
        networkUpdated = true;
    }
}