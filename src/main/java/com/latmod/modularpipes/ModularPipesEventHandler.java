package com.latmod.modularpipes;

import com.latmod.modularpipes.api_impl.PipeNetwork;
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
        if(event.phase == TickEvent.Phase.END)
        {
            PipeNetwork.get(event.world).update();
        }
    }

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event)
    {
        if(event.getWorld() instanceof WorldServer)
        {
            PipeNetwork.load(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldUnloaded(WorldEvent.Unload event)
    {
        if(event.getWorld() instanceof WorldServer)
        {
            PipeNetwork.unload(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldSaved(WorldEvent.Save event)
    {
        PipeNetwork.save(event.getWorld());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.player instanceof EntityPlayerMP)
        {
            PipeNetwork.get(((EntityPlayerMP) event.player).world).playerLoggedIn((EntityPlayerMP) event.player);
        }
    }
}