package com.latmod.modularpipes.block;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum EnumPipeSkin implements IStringSerializable
{
	NONE("none", 0, "gui.none", new ResourceLocation("minecraft:blocks/concrete_gray")),
	WHITE("white", 1, "item.fireworksCharge.white", new ResourceLocation("minecraft:blocks/concrete_powder_white")),
	ORANGE("orange", 2, "item.fireworksCharge.orange", new ResourceLocation("minecraft:blocks/concrete_powder_orange")),
	MAGENTA("magenta", 3, "item.fireworksCharge.magenta", new ResourceLocation("minecraft:blocks/concrete_powder_magenta")),
	LIGHT_BLUE("light_blue", 4, "item.fireworksCharge.lightBlue", new ResourceLocation("minecraft:blocks/concrete_powder_light_blue")),
	YELLOW("yellow", 5, "item.fireworksCharge.yellow", new ResourceLocation("minecraft:blocks/concrete_powder_yellow")),
	LIME("lime", 6, "item.fireworksCharge.lime", new ResourceLocation("minecraft:blocks/concrete_powder_lime")),
	PINK("pink", 7, "item.fireworksCharge.pink", new ResourceLocation("minecraft:blocks/concrete_powder_pink")),
	GRAY("gray", 8, "item.fireworksCharge.gray", new ResourceLocation("minecraft:blocks/concrete_powder_gray")),
	SILVER("silver", 9, "item.fireworksCharge.silver", new ResourceLocation("minecraft:blocks/concrete_powder_silver")),
	CYAN("cyan", 10, "item.fireworksCharge.cyan", new ResourceLocation("minecraft:blocks/concrete_powder_cyan")),
	PURPLE("purple", 11, "item.fireworksCharge.purple", new ResourceLocation("minecraft:blocks/concrete_powder_purple")),
	BLUE("blue", 12, "item.fireworksCharge.blue", new ResourceLocation("minecraft:blocks/concrete_powder_blue")),
	BROWN("brown", 13, "item.fireworksCharge.brown", new ResourceLocation("minecraft:blocks/concrete_powder_brown")),
	GREEN("green", 14, "item.fireworksCharge.green", new ResourceLocation("minecraft:blocks/concrete_powder_green")),
	RED("red", 15, "item.fireworksCharge.red", new ResourceLocation("minecraft:blocks/concrete_powder_red")),
	BLACK("black", 16, "item.fireworksCharge.black", new ResourceLocation("minecraft:blocks/concrete_powder_black")),
	BRICK("brick", 17, "tile.brick.name", new ResourceLocation("minecraft:blocks/brick")),
	ICE("ice", 18, "tile.ice.name", new ResourceLocation("minecraft:blocks/ice_packed")),
	MELON("melon", 19, "tile.melon.name", new ResourceLocation("minecraft:blocks/melon_top")),
	;

	public static final EnumPipeSkin[] VALUES = values();
	public static final EnumPipeSkin[] BY_ID = new EnumPipeSkin[32];
	public static final Map<String, EnumPipeSkin> BY_NAME = new HashMap<>();

	static
	{
		for (EnumPipeSkin skin : VALUES)
		{
			BY_ID[skin.id] = skin;
			BY_NAME.put(skin.name, skin);
		}
	}

	public static EnumPipeSkin byID(int id)
	{
		return id < 0 || id >= BY_ID.length || BY_ID[id] == null ? NONE : BY_ID[id];
	}

	public static EnumPipeSkin byName(String name)
	{
		EnumPipeSkin skin = BY_NAME.get(name);
		return skin == null ? NONE : skin;
	}

	private final String name;
	public final int id;
	public final String translationKey;
	public final ResourceLocation texture;

	EnumPipeSkin(String n, int i, String tk, ResourceLocation tex)
	{
		name = n;
		id = i;
		translationKey = tk;
		texture = tex;
	}

	@Override
	public String getName()
	{
		return name;
	}
}