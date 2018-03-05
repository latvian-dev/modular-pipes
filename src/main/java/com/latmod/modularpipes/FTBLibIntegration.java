package com.latmod.modularpipes;

import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.events.player.ForgePlayerDataEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.latmod.modularpipes.data.ModularPipesPlayerData;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBLibIntegration
{
	@SubscribeEvent
	public static void registerPlayerData(ForgePlayerDataEvent event)
	{
		event.register(ModularPipes.MOD_ID, new ModularPipesPlayerData(event.getPlayer()));
	}

	@SubscribeEvent
	public static void playerSettings(ForgePlayerConfigEvent event)
	{
		ModularPipesPlayerData data = ModularPipesPlayerData.get(event.getPlayer());
		event.getConfig().setGroupName(ModularPipes.MOD_ID, new TextComponentString(ModularPipes.MOD_NAME));
		event.getConfig().add(ModularPipes.MOD_ID, "dev_mode", data.devMode).setNameLangKey(ModularPipes.MOD_ID + ".general.dev_mode");
	}
}