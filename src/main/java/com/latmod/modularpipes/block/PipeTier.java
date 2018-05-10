package com.latmod.modularpipes.block;

import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import com.latmod.modularpipes.ModularPipesConfig;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum PipeTier implements IStringSerializable
{
	BASIC("basic", ModularPipesConfig.tiers.basic),
	IRON("iron", ModularPipesConfig.tiers.iron),
	GOLD("gold", ModularPipesConfig.tiers.gold),
	QUARTZ("quartz", ModularPipesConfig.tiers.quartz),
	LAPIS("lapis", ModularPipesConfig.tiers.lapis),
	ENDER("ender", ModularPipesConfig.tiers.ender),
	DIAMOND("diamond", ModularPipesConfig.tiers.diamond),
	STAR("star", ModularPipesConfig.tiers.star);

	public static final NameMap<PipeTier> NAME_MAP = NameMap.create(BASIC, values());

	private final String name;
	public final ModularPipesConfig.Tier config;
	public String speedString;

	PipeTier(String n, ModularPipesConfig.Tier c)
	{
		name = n;
		config = c;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void sync()
	{
		if (config.speed == (int) config.speed)
		{
			speedString = (int) config.speed + "x";
		}
		else
		{
			speedString = StringUtils.formatDouble(config.speed) + "x";
		}
	}
}