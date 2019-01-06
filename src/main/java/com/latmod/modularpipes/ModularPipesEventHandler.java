package com.latmod.modularpipes;

import com.latmod.modularpipes.block.BlockController;
import com.latmod.modularpipes.block.BlockPipeBasicMK1;
import com.latmod.modularpipes.block.BlockPipeBasicMK2;
import com.latmod.modularpipes.block.BlockPipeBasicMK3;
import com.latmod.modularpipes.block.BlockPipeDiamond;
import com.latmod.modularpipes.block.BlockPipeModular;
import com.latmod.modularpipes.block.ModularPipesBlocks;
import com.latmod.modularpipes.item.module.ItemModuleCrafting;
import com.latmod.modularpipes.item.module.ItemModuleExtract;
import com.latmod.modularpipes.item.module.ItemModuleFluidStorage;
import com.latmod.modularpipes.item.module.ItemModuleItemStorage;
import com.latmod.modularpipes.item.module.ItemModuleRightClickExtract;
import com.latmod.modularpipes.tile.PipeNetwork;
import com.latmod.modularpipes.tile.TileController;
import com.latmod.modularpipes.tile.TilePipeBasicMK1;
import com.latmod.modularpipes.tile.TilePipeBasicMK2;
import com.latmod.modularpipes.tile.TilePipeBasicMK3;
import com.latmod.modularpipes.tile.TilePipeDiamond;
import com.latmod.modularpipes.tile.TilePipeModular;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesEventHandler
{
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	private static Block withName(Block block, String name)
	{
		block.setCreativeTab(ModularPipes.TAB);
		block.setRegistryName(name);
		block.setTranslationKey(ModularPipes.MOD_ID + "." + name);
		return block;
	}

	private static Item withName(Item item, String name)
	{
		item.setCreativeTab(ModularPipes.TAB);
		item.setRegistryName(name);
		item.setTranslationKey(ModularPipes.MOD_ID + "." + name);
		return item;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> r = event.getRegistry();
		r.register(withName(new BlockController(), "controller"));
		r.register(withName(new BlockPipeModular(), "pipe_modular"));
		r.register(withName(new BlockPipeBasicMK1(MapColor.STONE), "pipe_cobblestone"));
		r.register(withName(new BlockPipeBasicMK1(MapColor.STONE), "pipe_granite"));
		r.register(withName(new BlockPipeBasicMK1(MapColor.STONE), "pipe_diorite"));
		r.register(withName(new BlockPipeBasicMK1(MapColor.STONE), "pipe_andesite"));
		r.register(withName(new BlockPipeBasicMK2(MapColor.BROWN), "pipe_brick"));
		r.register(withName(new BlockPipeBasicMK2(MapColor.QUARTZ), "pipe_quartz"));
		r.register(withName(new BlockPipeBasicMK2(MapColor.YELLOW), "pipe_endstone"));
		r.register(withName(new BlockPipeBasicMK3(MapColor.GOLD), "pipe_gold"));
		r.register(withName(new BlockPipeBasicMK3(MapColor.GOLD), "pipe_glowstone"));
		r.register(withName(new BlockPipeBasicMK3(MapColor.ADOBE), "pipe_magma"));
		r.register(withName(new BlockPipeDiamond(), "pipe_diamond"));

		GameRegistry.registerTileEntity(TileController.class, new ResourceLocation(ModularPipes.MOD_ID, "controller"));
		GameRegistry.registerTileEntity(TilePipeModular.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_modular"));
		GameRegistry.registerTileEntity(TilePipeBasicMK1.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_basic_mk1"));
		GameRegistry.registerTileEntity(TilePipeBasicMK2.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_basic_mk2"));
		GameRegistry.registerTileEntity(TilePipeBasicMK3.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_basic_mk3"));
		GameRegistry.registerTileEntity(TilePipeDiamond.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_diamond"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(new ItemBlock(ModularPipesBlocks.CONTROLLER).setRegistryName("controller"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_MODULAR).setRegistryName("pipe_modular"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_COBBLESTONE).setRegistryName("pipe_cobblestone"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_GRANITE).setRegistryName("pipe_granite"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_DIORITE).setRegistryName("pipe_diorite"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_ANDESITE).setRegistryName("pipe_andesite"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_BRICK).setRegistryName("pipe_brick"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_QUARTZ).setRegistryName("pipe_quartz"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_ENDSTONE).setRegistryName("pipe_endstone"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_GOLD).setRegistryName("pipe_gold"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_GLOWSTONE).setRegistryName("pipe_glowstone"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_MAGMA).setRegistryName("pipe_magma"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_DIAMOND).setRegistryName("pipe_diamond"));

		r.register(withName(new Item(), "module"));
		r.register(withName(new ItemModuleExtract(), "module_extract"));
		r.register(withName(new ItemModuleRightClickExtract(), "module_rightclick_extract"));
		r.register(withName(new ItemModuleCrafting(), "module_crafting"));
		r.register(withName(new ItemModuleItemStorage(), "module_item_storage"));
		r.register(withName(new ItemModuleFluidStorage(), "module_fluid_storage"));
	}

	@SubscribeEvent
	public static void attachWorldCap(AttachCapabilitiesEvent<World> event)
	{
		event.addCapability(WORLD_CAP_ID, new PipeNetwork(event.getObject()));
	}

	@SubscribeEvent
	public static void tickServerWorld(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			PipeNetwork.get(event.world).tick();
		}
	}
}