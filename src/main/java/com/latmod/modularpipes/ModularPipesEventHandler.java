package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.feed_the_beast.ftbl.api.events.registry.RegisterDataProvidersEvent;
import com.latmod.modularpipes.data.ModularPipesPlayerData;
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
@EventHandler
public class ModularPipesEventHandler
{
	@SubscribeEvent
	public static void registerPlayerDataProvider(RegisterDataProvidersEvent.Player event)
	{
		event.register(ModularPipesPlayerData.ID, ModularPipesPlayerData::new);
	}

	@SubscribeEvent
	public static void playerSettings(ForgePlayerSettingsEvent event)
	{
		ModularPipesPlayerData data = (ModularPipesPlayerData) event.getPlayer().getData(ModularPipesPlayerData.ID);

		if (data != null)
		{
			event.add(ModularPipes.MOD_ID, "dev_mode", data.devMode).setNameLangKey("modularpipes.general.dev_mode");
		}
	}

	@SubscribeEvent
	public static void onTickEvent(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			PipeNetwork.get(event.world).update();
		}
	}

	@SubscribeEvent
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		if (event.getWorld() instanceof WorldServer)
		{
			PipeNetwork.get(event.getWorld()).server().load();
		}
	}

	@SubscribeEvent
	public static void onWorldUnloaded(WorldEvent.Unload event)
	{
		if (event.getWorld() instanceof WorldServer)
		{
			PipeNetwork.get(event.getWorld()).server().unload();
		}
	}

	@SubscribeEvent
	public static void onWorldSaved(WorldEvent.Save event)
	{
		PipeNetwork.get(event.getWorld()).server().save();
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			PipeNetwork.get(event.player.world).server().playerLoggedIn(event.player);
		}
	}
}