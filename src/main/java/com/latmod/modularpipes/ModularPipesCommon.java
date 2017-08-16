package com.latmod.modularpipes;

import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.net.ModularPipesNet;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	public void preInit()
	{
		ModularPipesNet.init();
	}

	public PipeNetwork getClientNetwork(World world)
	{
		throw new IllegalStateException();
	}
}