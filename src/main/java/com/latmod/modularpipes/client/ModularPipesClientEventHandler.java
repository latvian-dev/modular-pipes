package com.latmod.modularpipes.client;

import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * @author LatvianModder
 */
public class ModularPipesClientEventHandler
{
    @SubscribeEvent
    public static void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        if(ClientPipeNetwork.INSTANCE != null)
        {
            ClientPipeNetwork.INSTANCE.clear();
            ClientPipeNetwork.INSTANCE = null;
        }
    }

    @SubscribeEvent
    public static void onTickEvent(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().world != null && !Minecraft.getMinecraft().isGamePaused())
        {
            PipeNetwork.get(Minecraft.getMinecraft().world).update();
        }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        if(ClientPipeNetwork.INSTANCE != null)
        {
            ClientPipeNetwork.INSTANCE.render(event.getPartialTicks());
        }
    }
}