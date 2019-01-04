package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@GameRegistry.ObjectHolder(ModularPipes.MOD_ID)
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesBlocks
{
	public static final Block CONTROLLER = Blocks.AIR;
	public static final Block PIPE_MODULAR = Blocks.AIR;
	public static final Block PIPE_COBBLESTONE = Blocks.AIR;
	public static final Block PIPE_GLOWSTONE = Blocks.AIR;
	public static final Block PIPE_DIAMOND = Blocks.AIR;
}