package com.latmod.modularpipes.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Side.CLIENT)
public class ModularPipesClientEventHandler
{
	@SubscribeEvent
	public static void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		if (ClientPipeNetwork.INSTANCE != null)
		{
			ClientPipeNetwork.INSTANCE.clear();
			ClientPipeNetwork.INSTANCE = null;
		}
	}

	@SubscribeEvent
	public static void onTickEvent(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && ClientUtils.MC.world != null && !ClientUtils.MC.isGamePaused())
		{
			PipeNetwork.get(ClientUtils.MC.world).update();
		}
	}

	@SubscribeEvent
	public static void onRenderTick(RenderWorldLastEvent event)
	{
		if (ClientPipeNetwork.INSTANCE != null)
		{
			ClientPipeNetwork.INSTANCE.render(event.getPartialTicks());
		}
	}
}