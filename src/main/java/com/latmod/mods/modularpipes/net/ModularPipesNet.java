package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.ModularPipes;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
public class ModularPipesNet
{
	public static final SimpleNetworkWrapper NET = new SimpleNetworkWrapper(ModularPipes.MOD_ID);

	public static void init()
	{
		NET.registerMessage(new MessageSendPaint.Handler(), MessageSendPaint.class, 1, Side.SERVER);
		NET.registerMessage(new MessageParticle.Handler(), MessageParticle.class, 2, Side.CLIENT);
	}
}