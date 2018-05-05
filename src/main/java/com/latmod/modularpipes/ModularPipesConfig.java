package com.latmod.modularpipes;

import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
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
		public final Tier basic = new Tier(0, "basic", 1, 1);
		public final Tier iron = new Tier(1, "iron", 2, 1);
		public final Tier gold = new Tier(2, "gold", 2, 2);
		public final Tier quartz = new Tier(3, "quartz", 3, 2);
		public final Tier lapis = new Tier(4, "lapis", 4, 3);
		public final Tier ender = new Tier(5, "ender", 5, 8);
		public final Tier diamond = new Tier(6, "diamond", 6, 10);
		public final Tier star = new Tier(7, "star", 6, 100);

		private final NameMap<Tier> nameMap = NameMap.create(basic, basic, iron, gold, quartz, lapis, ender, diamond, star);

		public NameMap<Tier> getNameMap()
		{
			return nameMap;
		}
	}

	public static class Tier
	{
		private final int index;
		private final String name;
		public int modules;
		public double speed;
		private String speedString;

		public Tier(int i, String n, int m, double s)
		{
			index = i;
			name = n;
			modules = m;
			speed = s;
		}

		public int getIndex()
		{
			return index;
		}

		public String toString()
		{
			return name;
		}

		public String getSpeedString()
		{
			return speedString;
		}
	}

	public static void sync()
	{
		ConfigManager.sync(ModularPipes.MOD_ID, Config.Type.INSTANCE);

		for (Tier tier : tiers.getNameMap())
		{
			if (tier.speed == (int) tier.speed)
			{
				tier.speedString = (int) tier.speed + "x";
			}
			else
			{
				tier.speedString = StringUtils.formatDouble(tier.speed) + "x";
			}
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