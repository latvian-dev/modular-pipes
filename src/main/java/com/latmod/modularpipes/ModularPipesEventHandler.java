package com.latmod.modularpipes;

import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author LatvianModder
 */
public class ModularPipesEventHandler
{
    @SubscribeEvent
    public static void onTickEvent(TickEvent.WorldTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && !event.world.isRemote)
        {
            PipeNetwork.get(event.world).update();
        }
    }

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event)
    {
        if(event.getWorld() instanceof WorldServer)
        {
            PipeNetwork.get(event.getWorld()).load();
        }
    }

    @SubscribeEvent
    public static void onWorldUnloaded(WorldEvent.Unload event)
    {
        if(event.getWorld() instanceof WorldServer)
        {
            PipeNetwork.get(event.getWorld()).unload();
        }
    }

    @SubscribeEvent
    public static void onWorldSaved(WorldEvent.Save event)
    {
        PipeNetwork.get(event.getWorld()).save();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.player instanceof EntityPlayerMP)
        {
            PipeNetwork.get(event.player.world).playerLoggedIn(event.player);
        }
    }
}