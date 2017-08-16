package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
	@Override
	public PipeNetwork getClientNetwork(World world)
	{
		if (ClientPipeNetwork.INSTANCE == null || world != ClientPipeNetwork.INSTANCE.world)
		{
			ClientPipeNetwork.INSTANCE = new ClientPipeNetwork(world);
		}

		return ClientPipeNetwork.INSTANCE;
	}
}