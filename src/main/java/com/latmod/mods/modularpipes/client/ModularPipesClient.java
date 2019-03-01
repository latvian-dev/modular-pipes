package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.ModularPipesCommon;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
	@Override
	public int getPipeLightValue(IBlockAccess world)
	{
		if (world instanceof World)
		{
			if (!((World) world).isRemote)
			{
				return 0;
			}
		}

		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT ? 15 : 0;
	}
}