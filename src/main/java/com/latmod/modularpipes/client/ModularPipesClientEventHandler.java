package com.latmod.modularpipes.client;

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
        ClientPipeNetwork.get().clear();
    }

    @SubscribeEvent
    public static void onTickEvent(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().world != null)
        {
            ClientPipeNetwork.get().update();
        }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        ClientPipeNetwork.get().render(event.getPartialTicks());
    }
}