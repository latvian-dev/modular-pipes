package com.latmod.modularpipes;

import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.net.MessageUpdateItems;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesEventHandler
{
    public static final List<TransportedItem> ITEMS = new ArrayList<>();
    public static int nextItemId = 0;
    private static final List<TransportedItem> UPDATE_CACHE = new ArrayList<>();
    private static final TIntArrayList REMOVE_CACHE = new TIntArrayList();

    @SubscribeEvent
    public static void onTickEvent(TickEvent.WorldTickEvent event)
    {
        if(event.world.provider.getDimension() == 0 && event.phase == TickEvent.Phase.END)
        {
            UPDATE_CACHE.clear();
            REMOVE_CACHE.clear();

            Iterator<TransportedItem> iterator = ITEMS.iterator();
            while(iterator.hasNext())
            {
                TransportedItem item = iterator.next();
                item.update();

                if(item.action == TransportedItem.Action.REMOVE)
                {
                    REMOVE_CACHE.add(item.id);
                    iterator.remove();
                }
                else if(item.action == TransportedItem.Action.UPDATE)
                {
                    UPDATE_CACHE.add(item);
                }
            }

            if(!UPDATE_CACHE.isEmpty() || REMOVE_CACHE.isEmpty())
            {
                ModularPipes.NET.sendToAll(new MessageUpdateItems(UPDATE_CACHE, REMOVE_CACHE));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event)
    {
        nextItemId = 0;
        ITEMS.clear();

        File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/modularpipes.dat");

        if(!file.exists())
        {
            return;
        }

        NBTTagCompound data;

        try
        {
            data = CompressedStreamTools.read(file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        NBTTagList list = data.getTagList("Items", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.tagCount(); i++)
        {
            TransportedItem item = new TransportedItem();
            item.deserializeNBT(list.getCompoundTagAt(i));

            if(item.action != TransportedItem.Action.REMOVE)
            {
                item.action = TransportedItem.Action.UPDATE;
                ITEMS.add(item);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldSaved(WorldEvent.Save event)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for(TransportedItem item : ITEMS)
        {
            list.appendTag(item.serializeNBT());
        }

        nbt.setTag("Items", list);

        File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/modularpipes.dat");

        try
        {
            CompressedStreamTools.write(nbt, file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void addItem(TransportedItem item)
    {
        item.id = ++nextItemId;
        item.action = TransportedItem.Action.UPDATE;

        if(nextItemId == 2000000000)
        {
            nextItemId = 0;
        }

        ITEMS.add(item);
    }
}