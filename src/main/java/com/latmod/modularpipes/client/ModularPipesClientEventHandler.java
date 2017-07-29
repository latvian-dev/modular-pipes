package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.FTBLibClientRegistryEvent;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.block.EnumTier;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
public class ModularPipesClientEventHandler
{
	@SubscribeEvent
	public static void registerClient(FTBLibClientRegistryEvent event)
	{
		ModularPipesConfig.initClient(event.getRegistry());
	}

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
		if (event.phase == TickEvent.Phase.END && FTBLibClient.MC.world != null && !FTBLibClient.MC.isGamePaused())
		{
			PipeNetwork.get(FTBLibClient.MC.world).update();
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

	@SubscribeEvent
	public static void onTexturesStitched(TextureStitchEvent.Post event)
	{
		for (EnumTier tier : EnumTier.NAME_MAP.values)
		{
			RenderModularPipe.SPRITES[tier.ordinal()] = event.getMap().getAtlasSprite(ModularPipes.MOD_ID + ":blocks/pipes/tier_" + tier.getName());
		}
	}
}