package com.latmod.modularpipes;

import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.net.MessageUpdateItems;
import com.latmod.modularpipes.util.BlockDimPos;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeNetwork implements INBTSerializable<NBTTagCompound>
{
    public static PipeNetwork INSTANCE;

    private final List<Path> pathList = new ArrayList<>();
    public final List<TransportedItem> items = new ArrayList<>();
    private int nextItemId = 0;
    private final List<TransportedItem> updateCache = new ArrayList<>();
    private final TIntArrayList removeCache = new TIntArrayList();

    public void clear()
    {
        nextItemId = 0;
        items.clear();
    }

    public void addOrUpdatePipe(BlockDimPos pos)
    {
    }

    public void removePipe(BlockDimPos pos)
    {
        Iterator<Path> iterator = pathList.iterator();

        while(iterator.hasNext())
        {
            Path p = iterator.next();

            if(p.contains(pos))
            {
                iterator.remove();
            }
        }
    }

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

    /*
    public List<TilePipe> findPipes(TilePipe source, boolean newList)
    {
        List<TilePipe> list;

        if(newList)
        {
            list = new ArrayList<>();
        }
        else
        {
            tempList.clear();
            list = tempList;
        }

        tempPosSet.clear();
        tempPosSet.add(source.getPos());

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            findPipes0(source.getWorld(), source.getPos().offset(facing), facing.getOpposite().getIndex(), list);
        }

        return list;
    }

    public void findPipes0(World world, BlockPos pos, int from, Collection<TilePipe> pipes)
    {
        IBlockState state = world.getBlockState(pos);

        if(!(state.getBlock() instanceof BlockPipe) || tempPosSet.contains(pos))
        {
            return;
        }

        tempPosSet.add(pos);

        if(state.getBlock().hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TilePipe)
            {
                pipes.add((TilePipe) tileEntity);
            }
        }

        for(int i = 0; i < 6; i++)
        {
            if(i != from)
            {
                findPipes0(world, pos.offset(EnumFacing.VALUES[i]), MathUtils.OPPOSITE[i], pipes);
            }
        }
    }
    */

    public boolean generatePath(ModuleContainer container, TransportedItem item)
    {
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

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for(TransportedItem item : items)
        {
            list.appendTag(item.serializeNBT());
        }

        nbt.setTag("Items", list);
        list = new NBTTagList();

        for(Path path : pathList)
        {
            list.appendTag(path.writeToNBT());
        }
        nbt.setTag("PathList", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            TransportedItem item = new TransportedItem();
            item.deserializeNBT(list.getCompoundTagAt(i));

            if(item.action != TransportedItem.Action.REMOVE)
            {
                item.action = TransportedItem.Action.UPDATE;
                items.add(item);
            }
        }

        list = nbt.getTagList("PathList", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            pathList.add(new Path(list.getCompoundTagAt(i)));
        }
    }

    public void sync()
    {
        ModularPipes.NET.sendToAll(new MessageUpdateItems(updateCache, removeCache));
    }

    public void syncOnLogin(EntityPlayerMP playerMP)
    {
        ModularPipes.NET.sendTo(new MessageUpdateItems(items, new TIntArrayList()), playerMP);
    }
}