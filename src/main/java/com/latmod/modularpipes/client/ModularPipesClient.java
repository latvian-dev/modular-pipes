package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
	@Override
	public void preInit()
	{
		super.preInit();
		ModularPipesClientConfig.sync();
	}

	@Override
	public PipeNetwork getClientNetwork(World world)
	{
		if (ClientPipeNetwork.INSTANCE == null || world != ClientPipeNetwork.INSTANCE.world)
		{
			ClientPipeNetwork.INSTANCE = new ClientPipeNetwork(world);
		}

		return ClientPipeNetwork.INSTANCE;
	}

	@Override
	public void networkUpdated()
	{
		super.networkUpdated();

		if (ClientUtils.MC.world != null)
		{
			getClientNetwork(ClientUtils.MC.world).networkUpdated = true;
		}
	}
}