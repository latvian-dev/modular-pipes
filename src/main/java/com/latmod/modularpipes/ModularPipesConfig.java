package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.gui.GuiLang;
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
	@Config.LangKey(GuiLang.LANG_GENERAL)
	public static final General general = new General();

	public static final Pipes pipes = new Pipes();

	public static class General
	{
		@Config.Comment("Allows players to use dev features, like network visualisation")
		public boolean dev_mode = false;
	}

	public static class Pipes
	{
		@Config.RangeDouble(min = 0, max = 100)
		@Config.Comment("Base speed")
		public double base_speed = 0.01;

		@Config.RangeInt(min = 1, max = 255)
		@Config.Comment("Maximum length of blocks that will be used to look for link paths")
		public int max_link_length = 250;

		@Config.RangeDouble(min = 1, max = 1000)
		@Config.Comment("Super Boost speed modifier")
		public double super_boost = 10;
	}

	public static void sync()
	{
		ConfigManager.sync(ModularPipes.MOD_ID, Config.Type.INSTANCE);
		ModularPipes.PROXY.networkUpdated();
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