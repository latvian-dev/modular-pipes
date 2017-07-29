package com.latmod.modularpipes.item;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@GameRegistry.ObjectHolder(ModularPipes.MOD_ID)
public class ModularPipesItems
{
	public static final Block PIPE_BASIC = Blocks.AIR;
	public static final Block PIPE_MODULAR = Blocks.AIR;
	public static final Block PIPE_NODE = Blocks.AIR;

	public static final Item MODULE = Items.AIR;
	public static final Item DEBUG = Items.AIR;

	public static final Item MODULE_EXTRACT = Items.AIR;
	public static final Item MODULE_RIGHTCLICK_EXTRACT = Items.AIR;
	public static final Item MODULE_CRAFTING = Items.AIR;
}