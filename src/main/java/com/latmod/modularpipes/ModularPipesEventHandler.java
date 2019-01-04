package com.latmod.modularpipes;

import com.latmod.modularpipes.block.BlockController;
import com.latmod.modularpipes.block.BlockPipeCobblestone;
import com.latmod.modularpipes.block.BlockPipeDiamond;
import com.latmod.modularpipes.block.BlockPipeModular;
import com.latmod.modularpipes.block.ModularPipesBlocks;
import com.latmod.modularpipes.item.module.ItemModuleCrafting;
import com.latmod.modularpipes.item.module.ItemModuleExtract;
import com.latmod.modularpipes.item.module.ItemModuleFluidStorage;
import com.latmod.modularpipes.item.module.ItemModuleItemStorage;
import com.latmod.modularpipes.item.module.ItemModuleRightClickExtract;
import com.latmod.modularpipes.tile.PipeNetwork;
import com.latmod.modularpipes.tile.TileCobblestonePipe;
import com.latmod.modularpipes.tile.TileController;
import com.latmod.modularpipes.tile.TileDiamondPipe;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
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
		r.register(withName(new BlockPipeCobblestone(), "pipe_cobblestone"));
		r.register(withName(new BlockPipeDiamond(), "pipe_diamond"));

		GameRegistry.registerTileEntity(TileController.class, new ResourceLocation(ModularPipes.MOD_ID, "controller"));
		GameRegistry.registerTileEntity(TileModularPipe.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_modular"));
		GameRegistry.registerTileEntity(TileCobblestonePipe.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_cobblestone"));
		GameRegistry.registerTileEntity(TileDiamondPipe.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_diamond"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(new ItemBlock(ModularPipesBlocks.CONTROLLER).setRegistryName("controller"));
		r.register(new ItemBlock(ModularPipesBlocks.PIPE_COBBLESTONE).setRegistryName("pipe_cobblestone"));
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