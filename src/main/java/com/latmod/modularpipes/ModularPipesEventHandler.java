package com.latmod.modularpipes;

import com.latmod.modularpipes.api.TransportedItem;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesEventHandler
{
    public static final List<TransportedItem> ITEMS = new ArrayList<>();
    public static final List<TransportedItem> ITEM_QUEUE = new ArrayList<>();
    public static int nextItemId = 0;

    @SubscribeEvent
    public static void onTickEvent(TickEvent.WorldTickEvent event)
    {
        if(event.world.provider.getDimension() == 0 && event.phase == TickEvent.Phase.END)
        {
            if(!ITEM_QUEUE.isEmpty())
            {
                ITEMS.addAll(ITEM_QUEUE);
                ITEM_QUEUE.clear();
            }

            for(TransportedItem item : ITEMS)
            {
                item.update();
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event)
    {
        nextItemId = 0;
        ITEMS.clear();
        ITEM_QUEUE.clear();

        NBTTagList list = new NBTTagList();//TODO: Load

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

    public static void addItem(TransportedItem item)
    {
        item.id = ++nextItemId;

        if(nextItemId == 2000000000)
        {
            nextItemId = 0;
        }
    }

    @SubscribeEvent
    public static void onWorldSaved(WorldEvent.Save event)
    {
        if(!ITEM_QUEUE.isEmpty())
        {
            ITEMS.addAll(ITEM_QUEUE);
            ITEM_QUEUE.clear();
        }

        NBTTagList list = new NBTTagList();

        for(TransportedItem item : ITEMS)
        {
            list.appendTag(item.serializeNBT());
        }
    }
}