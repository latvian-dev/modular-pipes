package com.latmod.modularpipes.tile;

import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum EnumDiamondPipeMode implements IStringSerializable
{
	IN("in"),
	OUT("out"),
	DISABLED("disabled");

	public static final EnumDiamondPipeMode[] VALUES = values();
	public static final String LANGUAGE_KEY = "tile.modularpipes.diamond_pipe.mode";

	private final String name;
	private final String languageKey;

	EnumDiamondPipeMode(String n)
	{
		name = n;
		languageKey = LANGUAGE_KEY + "." + name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getLanguageKey()
	{
		return languageKey;
	}
}