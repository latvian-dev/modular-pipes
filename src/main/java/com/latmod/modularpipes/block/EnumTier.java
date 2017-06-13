package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum EnumTier implements IStringSerializable
{
	BASIC("basic", 1D),
	IRON("iron", 1D),
	GOLD("gold", 1.5D),
	QUARTZ("quartz", 2D),
	LAPIS("lapis", 2.5D),
	ENDER("ender", 4D),
	EMERALD("emerald", 5D),
	STAR("star", 10D);

	public static final EnumTier[] VALUES = EnumTier.values();

	public static EnumTier getFromMeta(int m)
	{
		return VALUES[m & 7];
	}

	private final String name;
	public final int modules;
	public final PropertyDouble speed;

	EnumTier(String n, double d)
	{
		name = n;
		modules = Math.min(ordinal(), 6);
		speed = new PropertyDouble(d, 1D, 100D);
	}

	@Override
	public String getName()
	{
		return name;
	}
}