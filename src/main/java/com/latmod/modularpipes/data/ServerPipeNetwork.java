package com.latmod.modularpipes.data;

import com.latmod.modularpipes.net.MessageUpdateItems;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
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
    private final Map<BlockPos, Node> nodeMap = new HashMap<>();
    private final List<Link> linkList = new ArrayList<>();
    private final List<Link> linkListTemp = new ArrayList<>();
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
        nodeMap.clear();
        linkList.clear();
        nextItemId = 0;
    }

    public void save()
    {
        try
        {
            File dir = new File(((WorldServer) world).getChunkSaveLocation(), "data/modularpipes");

            File file = new File(dir, "modularpipes.dat");

            if(!file.exists() && nodeMap.isEmpty() && linkList.isEmpty() && items.isEmpty())
            {
                return;
            }

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            //ModularPipes.LOGGER.info("Saved pipe info to " + dir.getAbsolutePath());
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList list = new NBTTagList();

            for(Node node : getNodes())
            {
                list.appendTag(new NBTTagIntArray(new int[] {node.getX(), node.getY(), node.getZ()}));
            }

            nbt.setTag("Nodes", list);
            list = new NBTTagList();

            for(Link link : linkList)
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
            CompressedStreamTools.write(nbt, file);
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

        if(!file.exists())
        {
            return;
        }

        NBTTagCompound nbt;

        try
        {
            nbt = CompressedStreamTools.read(file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        NBTTagList list = nbt.getTagList("Nodes", Constants.NBT.TAG_INT_ARRAY);

        for(int i = 0; i < list.tagCount(); i++)
        {
            int[] pos = list.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                Node node = new Node(this, pos[0], pos[1], pos[2]);
                nodeMap.put(node, node);
            }
        }

        list = nbt.getTagList("Links", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            Link link = new Link(this);
            link.deserializeNBT(list.getCompoundTagAt(i));
            linkList.add(link);
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
        return nodeMap.get(pos);
    }

    @Override
    public Collection<Node> getNodes()
    {
        return nodeMap.values();
    }

    @Override
    public List<Link> getPathList(BlockPos pos, boolean useTempList)
    {
        List<Link> list = useTempList ? linkListTemp : new ArrayList<>();
        list.clear();

        for(Link path : linkList)
        {
            if(path.contains(pos))
            {
                list.add(path);
            }
        }

        return list;
    }

    @Override
    @Nullable
    public Link getBestPath(Node from, Node to)
    {
        List<Link> list = getPathList(from, true);

        if(list.isEmpty())
        {
            return null;
        }

        list.removeIf(new Link.PosPredicate(to, false, true));

        if(list.isEmpty())
        {
            return null;
        }
        else if(list.size() > 1)
        {
            list.sort(Link.COMPARATOR);
        }

        return list.get(0);
    }

    @Override
    public void addOrUpdatePipe(BlockPos pos, IBlockState state, IPipeBlock block)
    {
        if(!loaded)
        {
            return;
        }

        removePipe(pos, state, block);

        if(block.isNode(world, pos, state))
        {
            Node node = new Node(this, pos);
            nodeMap.put(node, node);

            for(EnumFacing facing : EnumFacing.VALUES)
            {
                IBlockState state1 = world.getBlockState(pos.offset(facing));

                if(state1.getBlock() instanceof IPipeBlock)
                {
                    Link link = findNode(node, facing);

                    if(link != null)
                    {
                        //ModularPipes.LOGGER.info("Found link " + link);
                        linkList.add(link);
                    }
                }
            }
        }
        else
        {
            //ModularPipes.LOGGER.info("Placed pipe @ " + pos);
        }
    }

    @Nullable
    private Link findNode(BlockPos start, EnumFacing facing)
    {
        List<BlockPos> list = new ArrayList<>();
        HashSet<BlockPos> set = new HashSet<>();
        double length = 0D;
        list.add(start);
        set.add(start);
        int actualLength = 0;
        BlockPos pos = start.offset(facing);
        EnumFacing source = facing.getOpposite();
        IBlockState state1;

        while(actualLength < 250) //TODO: Config option
        {
            state1 = world.getBlockState(pos);

            if(!(state1.getBlock() instanceof IPipeBlock))
            {
                //ModularPipes.LOGGER.warn("Block not a pipe @ " + pos);
                return null;
            }

            IPipeBlock pipe = (IPipeBlock) state1.getBlock();

            if(pipe.isNode(world, pos, state1))
            {
                list.add(pos);
                Link link = new Link(this);
                link.setPath(list, false);
                link.actualLength = actualLength + 2;
                link.length = length + 2D;
                //path.simplify();
                return link;
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
                    length += 1D / pipe.getSpeedModifier(world, pos, state1);
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

            actualLength++;
        }

        //ModularPipes.LOGGER.warn("Path too long!");
        return null;
    }

    @Override
    public void removePipe(BlockPos pos, IBlockState state, IPipeBlock block)
    {
        if(loaded)
        {
            if(block.isNode(world, pos, state))
            {
                nodeMap.remove(pos);
                linkList.removeIf(new Link.PosPredicate(pos, true, true));
            }
            else
            {
                linkList.removeIf(new Link.PosPredicate(pos, true, false));
            }
        }
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
    public boolean generatePath(ModuleContainer container, TransportedItem item)
    {
        for(Link link : getPathList(container.getTile().getPos(), true))
        {
            for(BlockPos pos : link.path)
            {
                //PipeNetwork.test(container.getTile().getWorld(), pos);
            }
        }

        return false;
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
    }

    @Override
    public void itemUpdated(int id, TransportedItem item)
    {
        updateCache.put(item.id, item);
    }

    public void playerLoggedIn(EntityPlayer player)
    {
        new MessageUpdateItems(items).sendTo(player);
    }
}