package com.latmod.modularpipes.client;

import com.latmod.modularpipes.api.TransportedItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesClientEventHandler
{
    public static final List<TransportedItem> ITEMS = new ArrayList<>();
    public static final List<TransportedItem> ITEM_QUEUE = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(Minecraft.getMinecraft().world == null)
        {
            if(!ITEMS.isEmpty())
            {
                ITEMS.clear();
                ITEM_QUEUE.clear();
            }

            return;
        }

        if(!ITEM_QUEUE.isEmpty())
        {
            ITEMS.addAll(ITEM_QUEUE);
            ITEM_QUEUE.clear();
        }

        for(TransportedItem item : ITEMS)
        {
            item.render(event.renderTickTime);
        }
    }
}