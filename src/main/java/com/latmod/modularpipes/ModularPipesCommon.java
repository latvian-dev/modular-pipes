package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.RecipeUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
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
		String id = ModularPipes.MOD_ID + ':';
		Ingredient basicPipe = RecipeUtils.getIngredient(ModularPipesItems.PIPE_BASIC);
		Ingredient blankModule = RecipeUtils.getIngredient(ModularPipesItems.MODULE);

		RecipeUtils.addRecipe(id + "pipe_basic", new ItemStack(ModularPipesItems.PIPE_BASIC, 8), 3,
				null, ODItems.GLASS_PANE_ANY, null,
				ODItems.COBBLE, ODItems.REDSTONE, ODItems.COBBLE,
				null, ODItems.GLASS_PANE_ANY, null);

		RecipeUtils.addCircularRecipe(id + "pipe_modular_0", new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, 0), ODItems.REDSTONE, basicPipe);

		Object[] items = {ODItems.IRON, ODItems.GOLD, ODItems.QUARTZ, ODItems.LAPIS, ODItems.ENDERPEARL, ODItems.EMERALD, ODItems.NETHERSTAR};

		for (int meta = 0; meta < 7; meta++)
		{
			RecipeUtils.addCircularRecipe(id + "pipe_modular_" + meta + 1, new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, meta + 1), items[meta], new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, meta));
		}

		RecipeUtils.addCircularRecipe(id + "module", new ItemStack(ModularPipesItems.MODULE, 8), basicPipe, ODItems.IRON);

		RecipeUtils.addRecipe(id + "module_crafting", new ItemStack(ModularPipesItems.Modules.CRAFTING), 1, ODItems.DIAMOND, blankModule, ODItems.CRAFTING_TABLE);
		RecipeUtils.addRecipe(id + "module_extract", new ItemStack(ModularPipesItems.Modules.EXTRACT), 1, Items.REPEATER, blankModule, Blocks.HOPPER);
		RecipeUtils.addRecipe(id + "module_rightclick_extract", new ItemStack(ModularPipesItems.Modules.RIGHTCLICK_EXTRACT), 1, Items.CLOCK, blankModule);
	}

	public PipeNetwork getClientNetwork(World world)
	{
		throw new IllegalStateException();
	}
}