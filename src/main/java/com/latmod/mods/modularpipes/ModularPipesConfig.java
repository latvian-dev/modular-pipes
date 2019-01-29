package com.latmod.mods.modularpipes;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
@Config(modid = ModularPipes.MOD_ID, category = "")
public class ModularPipesConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static final Pipes pipes = new Pipes();

	public static class General
	{
		@Config.Comment("Allows players to use dev features, like network visualisation")
		public boolean dev_mode = false;
	}

	public static class Pipes
	{
		@Config.RangeDouble(min = 0, max = 1)
		@Config.Comment("Base speed")
		public double base_speed = 0.05;
	}

	public static void sync()
	{
		ConfigManager.sync(ModularPipes.MOD_ID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(ModularPipes.MOD_ID))
		{
			sync();
		}
	}
}