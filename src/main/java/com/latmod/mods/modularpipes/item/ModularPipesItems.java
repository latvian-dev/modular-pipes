package com.latmod.mods.modularpipes.item;

import com.latmod.mods.modularpipes.ModularPipes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author LatvianModder
 */
@ObjectHolder(ModularPipes.MOD_ID)
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesItems
{
	public static final Item PIPE_TRANSPORT = Items.AIR;
	public static final Item PIPE_MODULAR_MK1 = Items.AIR;
	public static final Item PIPE_MODULAR_MK2 = Items.AIR;
	public static final Item PIPE_MODULAR_MK3 = Items.AIR;
	public static final Item TANK = Items.AIR;
	public static final Item MODULAR_STORAGE = Items.AIR;
	public static final Item MODULAR_TANK = Items.AIR;

	public static final Item PAINTER = Items.AIR;
	public static final Item MODULE = Items.AIR;
	public static final Item INPUT_PART = Items.AIR;
	public static final Item OUTPUT_PART = Items.AIR;
	public static final Item MODULE_ITEM_BASE = Items.AIR;
	public static final Item MODULE_ITEM_STORAGE = Items.AIR;
	public static final Item MODULE_ITEM_EXTRACT = Items.AIR;
	public static final Item MODULE_ITEM_INSERT = Items.AIR;
	public static final Item MODULE_FLUID_BASE = Items.AIR;
	public static final Item MODULE_FLUID_STORAGE = Items.AIR;
	public static final Item MODULE_FLUID_EXTRACT = Items.AIR;
	public static final Item MODULE_FLUID_INSERT = Items.AIR;
	public static final Item MODULE_CRAFTING = Items.AIR;
	public static final Item MODULE_ENERGY_INPUT = Items.AIR;
	public static final Item MODULE_ENERGY_OUTPUT = Items.AIR;
}