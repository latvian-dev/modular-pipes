package com.latmod.modularpipes.item;

import com.feed_the_beast.ftbl.lib.block.ItemBlockBase;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.block.BlockPipeBasic;
import com.latmod.modularpipes.block.BlockPipeBasicNode;
import com.latmod.modularpipes.block.EnumTier;
import com.latmod.modularpipes.client.RenderModularPipe;
import com.latmod.modularpipes.item.module.ItemModuleExtract;
import com.latmod.modularpipes.item.module.ItemModuleRightClickExtract;
import com.latmod.modularpipes.item.module.ModuleCrafting;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@GameRegistry.ObjectHolder(ModularPipes.MOD_ID)
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesItems
{
	public static final Block PIPE_BASIC = Blocks.AIR;
	public static final Block PIPE_MODULAR = Blocks.AIR;
	public static final Block PIPE_NODE = Blocks.AIR;

	public static final Item MODULE = Items.AIR;
	public static final Item DEBUG = Items.AIR;

	public static final Item MODULE_EXTRACT = Items.AIR;
	public static final Item MODULE_RIGHTCLICK_EXTRACT = Items.AIR;
	public static final Item MODULE_CRAFTING = Items.AIR;

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
				new ItemBlockBase(PIPE_BASIC),
				new ItemBlockBase(PIPE_MODULAR, true)
				{
					@Override
					public String getUnlocalizedName(ItemStack stack)
					{
						return super.getUnlocalizedName(stack) + '.' + EnumTier.getFromMeta(stack.getMetadata()).getName();
					}
				},
				new ItemBlockBase(PIPE_NODE, true),
				new ItemMPBase("module"),
				new ItemDebug("debug"),
				new ItemModuleExtract("module_extract"),
				new ItemModuleRightClickExtract("module_rightclick_extract"),
				new ModuleCrafting("module_crafting"));
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomStateMapper(PIPE_MODULAR, new StateMap.Builder().ignore(BlockModularPipe.TIER).build());

		ClientUtils.registerModel(PIPE_BASIC, 0, ModularPipes.MOD_ID + ":pipe_item#variant=basic");

		for (int m = 0; m < 8; m++)
		{
			ClientUtils.registerModel(PIPE_MODULAR, m, ModularPipes.MOD_ID + ":pipe_item#variant=tier_" + (m & 7));
		}

		ClientUtils.registerModel(PIPE_NODE, 0, PIPE_BASIC.getRegistryName() + "#model=none");

		ClientUtils.registerModel(MODULE);
		ClientUtils.registerModel(DEBUG);

		ClientUtils.registerModel(MODULE_EXTRACT);
		ClientUtils.registerModel(MODULE_RIGHTCLICK_EXTRACT);
		ClientUtils.registerModel(MODULE_CRAFTING);

		ClientRegistry.bindTileEntitySpecialRenderer(TileModularPipe.class, new RenderModularPipe());
	}
}