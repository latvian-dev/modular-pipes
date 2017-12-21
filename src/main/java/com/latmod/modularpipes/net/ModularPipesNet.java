package com.latmod.modularpipes.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.latmod.modularpipes.ModularPipes;

/**
 * @author LatvianModder
 */
public class ModularPipesNet
{
	static final NetworkWrapper NET = NetworkWrapper.newWrapper(ModularPipes.MOD_ID);

	public static void init()
	{
		NET.register(0, new MessageUpdateItems());
		NET.register(1, new MessageVisualizeNetwork());
	}
}