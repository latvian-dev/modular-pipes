package com.latmod.modularpipes.block;

import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public final class PipeSkin
{
	private static final Map<String, PipeSkin> MAP0 = new HashMap<>();
	public static final Map<String, PipeSkin> MAP = Collections.unmodifiableMap(MAP0);

	public static PipeSkin register(String name, String translationKey, ResourceLocation texture)
	{
		PipeSkin skin = new PipeSkin(name, translationKey, texture);
		MAP0.put(skin.name, skin);
		return skin;
	}

	public static final PipeSkin NONE = register("none", "gui.none", new ResourceLocation("minecraft:blocks/concrete_gray"));

	public static PipeSkin byName(String name)
	{
		if (name.isEmpty())
		{
			return NONE;
		}

		PipeSkin skin = MAP0.get(name);
		return skin == null ? NONE : skin;
	}

	public final String name;
	public final String translationKey;
	public final ResourceLocation texture;

	private PipeSkin(String n, String tk, ResourceLocation tex)
	{
		name = n;
		translationKey = tk;
		texture = tex;
	}

	public boolean isNone()
	{
		return this == NONE;
	}
}