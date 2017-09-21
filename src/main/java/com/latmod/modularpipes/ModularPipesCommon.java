package com.latmod.modularpipes;

import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.ServerPipeNetwork;
import com.latmod.modularpipes.net.ModularPipesNet;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	private static final Consumer<ServerPipeNetwork> UPDATE_NETWORK = PipeNetwork::markDirty;

	public void preInit()
	{
		ModularPipesNet.init();
	}

	public PipeNetwork getClientNetwork(World world)
	{
		throw new IllegalStateException();
	}

	public void networkUpdated()
	{
		ServerPipeNetwork.NETWORK_MAP.values().forEach(UPDATE_NETWORK);
	}
}