package com.latmod.modularpipes.api_impl;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.api.ILink;
import com.latmod.modularpipes.api.INode;
import com.latmod.modularpipes.api.IPipeBlock;
import com.latmod.modularpipes.api.IPipeNetwork;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.net.MessageUpdateItems;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class PipeNetwork implements IPipeNetwork
{
    private static final TIntObjectHashMap<PipeNetwork> NETWORK_MAP = new TIntObjectHashMap<>();

    /*
    public static PipeNetwork get(int dim)
    {
        PipeNetwork net = NETWORK_MAP.get(dim);

        if(net == null)
        {
            net = new PipeNetwork(dim);
            NETWORK_MAP.put(dim, net);
        }

        return net;
    }
    */

    public static PipeNetwork get(World world)
    {
        if(world.isRemote)
        {
            return ModularPipes.PROXY.getClientNetwork();
        }

        int dim = world.provider.getDimension();
        PipeNetwork net = NETWORK_MAP.get(dim);

        if(net == null)
        {
            net = new PipeNetwork(dim);
            net.setWorld(world);
            NETWORK_MAP.put(dim, net);
        }

        return net;
    }

    public static void clearAll()
    {
        NETWORK_MAP.clear();
    }

    public static void load(World world)
    {
        PipeNetwork network = get(world);
        network.setWorld(world);
        network.clear();

        File dir = new File(((WorldServer) world).getChunkSaveLocation(), "data/modularpipes");
        ModularPipes.LOGGER.info("Loading pipe info from " + dir.getAbsolutePath());

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

        NBTTagList list = nbt.getTagList("Nodes", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            int[] pos = list.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                INode node = new Node(network, new BlockPos(pos[0], pos[1], pos[2]));
                network.nodeMap.put(node.getPos(), node);
            }
        }

        list = nbt.getTagList("PathList", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            network.linkList.add(new Link(network, list.getCompoundTagAt(i)));
        }

        list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            TransportedItem item = new TransportedItem();
            item.deserializeNBT(list.getCompoundTagAt(i));

            if(item.action != TransportedItem.Action.REMOVE)
            {
                item.action = TransportedItem.Action.UPDATE;
                network.items.add(item);
            }
        }
    }

    public static void save(World world)
    {
        try
        {
            PipeNetwork network = get(world);
            File dir = new File(((WorldServer) world).getChunkSaveLocation(), "data/modularpipes");

            File file = new File(dir, "modularpipes.dat");

            if(!file.exists() && network.nodeMap.isEmpty() && network.linkList.isEmpty() && network.items.isEmpty())
            {
                return;
            }

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            ModularPipes.LOGGER.info("Saved pipe info to " + dir.getAbsolutePath());
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagCompound nbt1;
            NBTTagList list = new NBTTagList();

            for(INode node : network.getNodes())
            {
                //nbt1 = new NBTTagCompound();
                //nbt1.setIntArray("Pos", new int[] {node.getPos().getX(), node.getPos().getY(), node.getPos().getZ()});
                //list.appendTag(nbt1);
                list.appendTag(new NBTTagIntArray(new int[] {node.getPos().getX(), node.getPos().getY(), node.getPos().getZ()}));
            }

            nbt.setTag("Nodes", list);
            list = new NBTTagList();

            for(ILink link : network.linkList)
            {
                //link.simplify();

                nbt1 = new NBTTagCompound();
                NBTTagList list1 = new NBTTagList();

                for(BlockPos pos : link.getPath())
                {
                    list1.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
                }

                nbt1.setTag("Link", list1);
                nbt1.setFloat("Length", link.getLength());
                nbt1.setFloat("ActualLength", link.getActualLength());

                list1.appendTag(nbt1);
            }
            nbt.setTag("PathList", list);
            list = new NBTTagList();

            for(TransportedItem item : network.items)
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

    public static void unload(World world)
    {
        PipeNetwork network = get(world);
        network.setWorld(null);
        NETWORK_MAP.remove(network.dimension);
    }

    private static final ItemStack FIREWORKS_ITEM = new ItemStack(Items.FIREWORKS);

    public static void test(World world, BlockPos pos)
    {
        if(!world.isRemote)
        {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, FIREWORKS_ITEM);
            world.spawnEntity(entityfireworkrocket);
        }
    }

    // End of static //

    private final int dimension;
    private World world;
    private final Map<BlockPos, INode> nodeMap = new HashMap<>();
    private final List<ILink> linkList = new ArrayList<>();
    private final List<ILink> linkListTemp = new ArrayList<>();
    public final List<TransportedItem> items = new ArrayList<>();
    private int nextItemId = 0;
    private final List<TransportedItem> updateCache = new ArrayList<>();
    private final TIntArrayList removeCache = new TIntArrayList();

    public PipeNetwork(int dim)
    {
        dimension = dim;
    }

    @Override
    public int getDimension()
    {
        return dimension;
    }

    public void setWorld(@Nullable World w)
    {
        world = w;

        for(INode node : getNodes())
        {
            node.clearCache();
        }
    }

    public void clear()
    {
        nodeMap.clear();
        linkList.clear();
        items.clear();
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    @Nullable
    public INode getNode(BlockPos pos)
    {
        return nodeMap.get(pos);
    }

    @Override
    public void setNode(BlockPos pos, @Nullable INode node)
    {
        if(node == null)
        {
            nodeMap.remove(pos);
            ModularPipes.LOGGER.info("Node @ " + pos + " removed");
        }
        else
        {
            nodeMap.put(pos, node);
            ModularPipes.LOGGER.info("Node @ " + pos + " placed");
        }
    }

    @Override
    public Collection<INode> getNodes()
    {
        return nodeMap.values();
    }

    @Override
    public List<ILink> getPathList(BlockPos pos, boolean useTempList)
    {
        linkListTemp.clear();

        for(ILink path : linkList)
        {
            if(path.contains(pos))
            {
                linkListTemp.add(path);
            }
        }

        return linkListTemp;
    }

    @Override
    @Nullable
    public ILink getBestPath(BlockPos from, BlockPos to)
    {
        List<ILink> list = getPathList(from, true);

        if(list.isEmpty())
        {
            return null;
        }

        Iterator<ILink> iterator = list.iterator();

        while(iterator.hasNext())
        {
            if(!iterator.next().contains(to))
            {
                iterator.remove();
            }
        }

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
    public void addOrUpdatePipe(BlockPos pos, IBlockState state)
    {
        if(world == null || world.isRemote || !(state.getBlock() instanceof IPipeBlock))
        {
            return;
        }

        removeLinkAt(pos, state);

        if(((IPipeBlock) state.getBlock()).isNode(world, pos, state))
        {
            INode node = new Node(this, pos);

            for(EnumFacing facing : EnumFacing.VALUES)
            {
                IBlockState state1 = world.getBlockState(pos.offset(facing));

                if(state1.getBlock() instanceof IPipeBlock)
                {
                    ILink link = findNode(node, facing);

                    if(link != null)
                    {
                        ModularPipes.LOGGER.info("Found link " + link);
                        linkList.add(link);
                    }
                }
            }
        }
        else
        {
            ModularPipes.LOGGER.info("Placed pipe @ " + pos);
        }
    }

    @Nullable
    private ILink findNode(INode start, EnumFacing facing)
    {
        List<BlockPos> list = new ArrayList<>();
        HashSet<BlockPos> set = new HashSet<>();
        float length = 0F;
        list.add(start.getPos());
        set.add(start.getPos());
        int actualLength = 0;
        BlockPos pos = start.getPos().offset(facing);
        EnumFacing source = facing.getOpposite();
        IBlockState state1;

        while(actualLength < 256) //TODO: Config option
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
                Link link = new Link(this, UUID.randomUUID());
                link.setPath(list);
                link.setActualLength(actualLength);
                link.setLength(length);
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
                    length += 1F / pipe.getSpeedModifier(world, pos, state1);
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
    public void removeLinkAt(BlockPos pos, IBlockState state)
    {
        Iterator<ILink> iterator = linkList.iterator();

        while(iterator.hasNext())
        {
            ILink p = iterator.next();

            if(p.contains(pos))
            {
                ModularPipes.LOGGER.info("Link " + p.getId() + " removed from " + pos);
                //p.onRemoved(worldIn);
                iterator.remove();
            }
        }

        for(int i = 0; i < linkList.size(); i++)
        {
            ILink link = linkList.get(i);
        }
    }

    @Override
    public void addItem(TransportedItem item)
    {
        item.id = ++nextItemId;
        item.action = TransportedItem.Action.UPDATE;

        if(nextItemId == 2000000000)
        {
            nextItemId = 0;
        }

        items.add(item);
    }

    @Override
    public boolean generatePath(ModuleContainer container, TransportedItem item)
    {
        for(ILink link : getPathList(container.getTile().getPos(), true))
        {
            for(BlockPos pos : link.getPath())
            {
                PipeNetwork.test(container.getTile().getWorld(), pos);
            }
        }

        return false;
    }

    public void update()
    {
        updateCache.clear();
        removeCache.clear();

        Iterator<TransportedItem> iterator = items.iterator();
        while(iterator.hasNext())
        {
            TransportedItem item = iterator.next();
            item.update();

            if(item.action == TransportedItem.Action.REMOVE)
            {
                removeCache.add(item.id);
                iterator.remove();
            }
            else if(item.action == TransportedItem.Action.UPDATE)
            {
                updateCache.add(item);
            }
        }

        if(!updateCache.isEmpty() || removeCache.isEmpty())
        {
            sync();
        }
    }

    public void sync()
    {
        ModularPipes.NET.sendToAll(new MessageUpdateItems(updateCache, removeCache));
    }

    public void playerLoggedIn(EntityPlayerMP playerMP)
    {
        ModularPipes.NET.sendTo(new MessageUpdateItems(items, new TIntArrayList()), playerMP);
    }
}