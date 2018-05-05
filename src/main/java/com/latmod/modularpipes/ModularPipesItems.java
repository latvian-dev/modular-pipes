package com.latmod.modularpipes;

import com.feed_the_beast.ftblib.lib.block.ItemBlockBase;
import com.latmod.modularpipes.block.BlockBasicPipe;
import com.latmod.modularpipes.block.BlockController;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.client.ModelPipe;
import com.latmod.modularpipes.item.ItemDebug;
import com.latmod.modularpipes.item.ItemMPBase;
import com.latmod.modularpipes.item.module.ItemModuleCrafting;
import com.latmod.modularpipes.item.module.ItemModuleExtract;
import com.latmod.modularpipes.item.module.ItemModuleFluidStorage;
import com.latmod.modularpipes.item.module.ItemModuleItemStorage;
import com.latmod.modularpipes.item.module.ItemModuleRightClickExtract;
import com.latmod.modularpipes.tile.TileBasicPipe;
import com.latmod.modularpipes.tile.TileController;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
@GameRegistry.ObjectHolder(ModularPipes.MOD_ID)
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesItems
{
	public static final Block CONTROLLER = Blocks.AIR;

	public static final Block PIPE_BASIC = Blocks.AIR;
	public static final Block PIPE_MODULAR_BASIC = Blocks.AIR;
	public static final Block PIPE_MODULAR_IRON = Blocks.AIR;
	public static final Block PIPE_MODULAR_GOLD = Blocks.AIR;
	public static final Block PIPE_MODULAR_QUARTZ = Blocks.AIR;
	public static final Block PIPE_MODULAR_LAPIS = Blocks.AIR;
	public static final Block PIPE_MODULAR_ENDER = Blocks.AIR;
	public static final Block PIPE_MODULAR_DIAMOND = Blocks.AIR;
	public static final Block PIPE_MODULAR_STAR = Blocks.AIR;

	public static final Block PIPE_BASIC_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_BASIC_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_IRON_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_GOLD_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_QUARTZ_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_LAPIS_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_ENDER_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_DIAMOND_OPAQUE = Blocks.AIR;
	public static final Block PIPE_MODULAR_STAR_OPAQUE = Blocks.AIR;

	public static final Item MODULE = Items.AIR;
	public static final Item DEBUG = Items.AIR;

	public static final Item FILTER_OR = Items.AIR;
	public static final Item FILTER_AND = Items.AIR;
	public static final Item FILTER_ORE_DICTIONARY = Items.AIR;
	public static final Item FILTER_TOOL = Items.AIR;

	public static final Item MODULE_EXTRACT = Items.AIR;
	public static final Item MODULE_RIGHTCLICK_EXTRACT = Items.AIR;
	public static final Item MODULE_CRAFTING = Items.AIR;
	public static final Item MODULE_ITEM_STORAGE = Items.AIR;
	public static final Item MODULE_FLUID_STORAGE = Items.AIR;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();
		r.register(new BlockController("controller"));
		r.register(new BlockBasicPipe("pipe_basic", false));

		for (ModularPipesConfig.Tier tier : ModularPipesConfig.tiers.getNameMap())
		{
			r.register(new BlockModularPipe("pipe_modular_" + tier, tier, false));
		}

		r.register(new BlockBasicPipe("pipe_basic_opaque", true));

		for (ModularPipesConfig.Tier tier : ModularPipesConfig.tiers.getNameMap())
		{
			r.register(new BlockModularPipe("pipe_modular_" + tier + "_opaque", tier, true));
		}

		GameRegistry.registerTileEntity(TileController.class, "modularpipes:controller");
		GameRegistry.registerTileEntity(TileBasicPipe.class, "modularpipes:pipe_basic");
		GameRegistry.registerTileEntity(TileModularPipe.class, "modularpipes:pipe_modular");
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new ItemBlockBase(CONTROLLER),
				new ItemBlockBase(PIPE_BASIC),
				new ItemBlockBase(PIPE_MODULAR_BASIC),
				new ItemBlockBase(PIPE_MODULAR_IRON),
				new ItemBlockBase(PIPE_MODULAR_GOLD),
				new ItemBlockBase(PIPE_MODULAR_QUARTZ),
				new ItemBlockBase(PIPE_MODULAR_LAPIS),
				new ItemBlockBase(PIPE_MODULAR_ENDER),
				new ItemBlockBase(PIPE_MODULAR_DIAMOND),
				new ItemBlockBase(PIPE_MODULAR_STAR),
				new ItemBlockBase(PIPE_BASIC_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_BASIC_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_IRON_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_GOLD_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_QUARTZ_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_LAPIS_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_ENDER_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_DIAMOND_OPAQUE),
				new ItemBlockBase(PIPE_MODULAR_STAR_OPAQUE),
				new ItemMPBase("module"),
				new ItemDebug("debug"),
				new ItemModuleExtract("module_extract"),
				new ItemModuleRightClickExtract("module_rightclick_extract"),
				new ItemModuleCrafting("module_crafting"),
				new ItemModuleItemStorage("module_item_storage"),
				new ItemModuleFluidStorage("module_fluid_storage"));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(ModelPipe.INSTANCE);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CONTROLLER), 0, new ModelResourceLocation(CONTROLLER.getRegistryName(), "error=false"));

		registerPipe(PIPE_BASIC);
		registerPipe(PIPE_MODULAR_BASIC);
		registerPipe(PIPE_MODULAR_IRON);
		registerPipe(PIPE_MODULAR_GOLD);
		registerPipe(PIPE_MODULAR_QUARTZ);
		registerPipe(PIPE_MODULAR_LAPIS);
		registerPipe(PIPE_MODULAR_ENDER);
		registerPipe(PIPE_MODULAR_DIAMOND);
		registerPipe(PIPE_MODULAR_STAR);

		registerPipe(PIPE_BASIC_OPAQUE);
		registerPipe(PIPE_MODULAR_BASIC_OPAQUE);
		registerPipe(PIPE_MODULAR_IRON_OPAQUE);
		registerPipe(PIPE_MODULAR_GOLD_OPAQUE);
		registerPipe(PIPE_MODULAR_QUARTZ_OPAQUE);
		registerPipe(PIPE_MODULAR_LAPIS_OPAQUE);
		registerPipe(PIPE_MODULAR_ENDER_OPAQUE);
		registerPipe(PIPE_MODULAR_DIAMOND_OPAQUE);
		registerPipe(PIPE_MODULAR_STAR_OPAQUE);

		ModelLoader.setCustomModelResourceLocation(MODULE, 0, new ModelResourceLocation(MODULE.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(DEBUG, 0, new ModelResourceLocation(DEBUG.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MODULE_EXTRACT, 0, new ModelResourceLocation(MODULE_EXTRACT.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MODULE_RIGHTCLICK_EXTRACT, 0, new ModelResourceLocation(MODULE_RIGHTCLICK_EXTRACT.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MODULE_CRAFTING, 0, new ModelResourceLocation(MODULE_CRAFTING.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MODULE_ITEM_STORAGE, 0, new ModelResourceLocation(MODULE_ITEM_STORAGE.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(MODULE_FLUID_STORAGE, 0, new ModelResourceLocation(MODULE_FLUID_STORAGE.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private static void registerPipe(Block block)
	{
		ModelLoader.setCustomStateMapper(block, ModelPipe.INSTANCE);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, ModelPipe.INSTANCE.ID);
	}
}