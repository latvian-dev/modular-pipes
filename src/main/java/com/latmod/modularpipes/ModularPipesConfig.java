package com.latmod.modularpipes;

import com.latmod.modularpipes.block.PipeTier;
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
	public static final Tiers tiers = new Tiers();

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

	public static class Tiers
	{
		public final Tier basic = new Tier(1, 1);
		public final Tier iron = new Tier(2, 1);
		public final Tier gold = new Tier(2, 2);
		public final Tier quartz = new Tier(3, 2);
		public final Tier lapis = new Tier(4, 3);
		public final Tier ender = new Tier(5, 8);
		public final Tier diamond = new Tier(6, 10);
		public final Tier star = new Tier(6, 100);
	}

	public static class Tier
	{
		public int modules;
		public double speed;

		public Tier(int m, double s)
		{
			modules = m;
			speed = s;
		}
	}

	public static void sync()
	{
		ConfigManager.sync(ModularPipes.MOD_ID, Config.Type.INSTANCE);

		for (PipeTier tier : PipeTier.NAME_MAP)
		{
			tier.sync();
		}

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