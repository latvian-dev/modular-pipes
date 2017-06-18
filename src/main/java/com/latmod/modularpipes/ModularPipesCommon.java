package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	public static final CreativeTabs TAB = new CreativeTabs(ModularPipes.MOD_ID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, 7);
		}
	};

	public void onPreInit()
	{
		LMUtils.register(ModularPipesItems.PIPE_BASIC);
		LMUtils.register(ModularPipesItems.PIPE_MODULAR);
		LMUtils.register(ModularPipesItems.PIPE_NODE);

		LMUtils.register(ModularPipesItems.MODULE);
		LMUtils.register(ModularPipesItems.DEBUG);

		for (Item m : ModularPipesItems.Modules.LIST)
		{
			LMUtils.register(m);
		}

		GameRegistry.registerTileEntity(TileModularPipe.class, ModularPipes.MOD_ID + ":pipe_modular");

		ModularPipesCaps.init();
		ModularPipesNet.init();
	}

	public void onInit()
	{
		MinecraftForge.EVENT_BUS.register(ModularPipesEventHandler.class);
	}

	public PipeNetwork getClientNetwork(World world)
	{
		throw new IllegalStateException();
	}
}