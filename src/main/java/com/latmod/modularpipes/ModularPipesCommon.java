package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.block.ItemBlockBase;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.block.BlockPipeBasic;
import com.latmod.modularpipes.block.BlockPipeBasicNode;
import com.latmod.modularpipes.block.EnumTier;
import com.latmod.modularpipes.data.Module;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ItemDebug;
import com.latmod.modularpipes.item.ItemMPBase;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.item.module.ModuleCrafting;
import com.latmod.modularpipes.item.module.ModuleExtract;
import com.latmod.modularpipes.item.module.ModuleRightClickExtract;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesCommon
{
	public static final List<Item> MODULE_LIST = new ArrayList<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
				new BlockPipeBasic("pipe_basic", MapColor.GRAY),
				new BlockModularPipe("pipe_modular"),
				new BlockPipeBasicNode("pipe_node"));

		GameRegistry.registerTileEntity(TileModularPipe.class, ModularPipes.MOD_ID + ":pipe_modular");
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new ItemBlockBase(ModularPipesItems.PIPE_BASIC),
				new ItemBlockBase(ModularPipesItems.PIPE_MODULAR, true)
				{
					@Override
					public String getUnlocalizedName(ItemStack stack)
					{
						return super.getUnlocalizedName(stack) + '.' + EnumTier.getFromMeta(stack.getMetadata()).getName();
					}
				},
				new ItemBlockBase(ModularPipesItems.PIPE_NODE, true),
				new ItemMPBase("module"),
				new ItemDebug("debug"),

				add("extract", new ModuleExtract()),
				add("rightclick_extract", new ModuleRightClickExtract()),
				add("crafting", new ModuleCrafting()));
	}

	private static ItemModule add(String id, Module module)
	{
		ItemModule m = new ItemModule(id, module);
		MODULE_LIST.add(m);
		return m;
	}

	public void preInit()
	{
		ModularPipesCaps.init();
		ModularPipesNet.init();
	}

	public PipeNetwork getClientNetwork(World world)
	{
		throw new IllegalStateException();
	}
}