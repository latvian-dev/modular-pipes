package com.latmod.modularpipes;

import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.ServerPipeNetwork;
import com.latmod.modularpipes.net.ModularPipesNet;
import gnu.trove.procedure.TObjectProcedure;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	private static final TObjectProcedure<ServerPipeNetwork> UPDATE_NETWORK = network ->
	{
		network.networkUpdated = true;
		return true;
	};

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
		ServerPipeNetwork.NETWORK_MAP.forEachValue(UPDATE_NETWORK);
	}
}