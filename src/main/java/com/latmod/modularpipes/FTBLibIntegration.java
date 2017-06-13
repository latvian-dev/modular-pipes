package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.events.FTBLibClientRegistryEvent;
import com.feed_the_beast.ftbl.api.events.FTBLibRegistryEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.latmod.modularpipes.data.ModularPipesPlayerData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public enum FTBLibIntegration implements IFTBLibPlugin
{
	@FTBLibPlugin
	INSTANCE;

	public static FTBLibAPI API;

	@Override
	public void init(FTBLibAPI api)
	{
		API = api;
		MinecraftForge.EVENT_BUS.register(FTBLibIntegration.class);
	}

	@SubscribeEvent
	public static void registerCommon(FTBLibRegistryEvent event)
	{
		ModularPipesConfig.init(event.getRegistry());
		event.getRegistry().addPlayerDataProvider(ModularPipesPlayerData.ID, ModularPipesPlayerData::new);
	}

	@SubscribeEvent
	public static void registerClient(FTBLibClientRegistryEvent event)
	{
		ModularPipesConfig.initClient(event.getRegistry());
	}

	@SubscribeEvent
	public static void playerSettings(ForgePlayerSettingsEvent event)
	{
		ModularPipesPlayerData data = (ModularPipesPlayerData) event.getPlayer().getData(ModularPipesPlayerData.ID);

		if (data != null)
		{
			event.add(ModularPipes.MOD_ID, "dev_mode", data.devMode).setNameLangKey("config.modularpipes.dev_mode.name").setInfoLangKey("config.modularpipes.dev_mode.info");
		}
	}
}