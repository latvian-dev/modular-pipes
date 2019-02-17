package com.latmod.mods.modularpipes;

import com.latmod.mods.modularpipes.block.BlockModularStorage;
import com.latmod.mods.modularpipes.block.BlockModularTank;
import com.latmod.mods.modularpipes.block.BlockPipeModular;
import com.latmod.mods.modularpipes.block.BlockPipeTransport;
import com.latmod.mods.modularpipes.block.BlockTank;
import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.ModularPipesBlocks;
import com.latmod.mods.modularpipes.item.ItemBlockPipe;
import com.latmod.mods.modularpipes.item.ItemBlockTank;
import com.latmod.mods.modularpipes.item.ItemModule;
import com.latmod.mods.modularpipes.item.ItemPainter;
import com.latmod.mods.modularpipes.item.module.ModuleCrafting;
import com.latmod.mods.modularpipes.item.module.energy.ModuleEnergyInput;
import com.latmod.mods.modularpipes.item.module.energy.ModuleEnergyOutput;
import com.latmod.mods.modularpipes.item.module.fluid.ModuleFluidExtract;
import com.latmod.mods.modularpipes.item.module.fluid.ModuleFluidHandler;
import com.latmod.mods.modularpipes.item.module.fluid.ModuleFluidInsert;
import com.latmod.mods.modularpipes.item.module.fluid.ModuleFluidStorage;
import com.latmod.mods.modularpipes.item.module.item.ModuleItemExtract;
import com.latmod.mods.modularpipes.item.module.item.ModuleItemHandler;
import com.latmod.mods.modularpipes.item.module.item.ModuleItemInsert;
import com.latmod.mods.modularpipes.item.module.item.ModuleItemStorage;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TileModularStorageCore;
import com.latmod.mods.modularpipes.tile.TileModularStoragePart;
import com.latmod.mods.modularpipes.tile.TileModularTankCore;
import com.latmod.mods.modularpipes.tile.TileModularTankPart;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK2;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK3;
import com.latmod.mods.modularpipes.tile.TilePipeTransport;
import com.latmod.mods.modularpipes.tile.TileTank;
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
		r.register(withName(new BlockPipeTransport(), "pipe_transport"));
		r.register(withName(new BlockPipeModular(EnumMK.MK1), "pipe_modular_mk1"));
		r.register(withName(new BlockPipeModular(EnumMK.MK2), "pipe_modular_mk2"));
		r.register(withName(new BlockPipeModular(EnumMK.MK3), "pipe_modular_mk3"));
		r.register(withName(new BlockTank(), "tank"));
		r.register(withName(new BlockModularStorage(), "modular_storage"));
		r.register(withName(new BlockModularTank(), "modular_tank"));

		GameRegistry.registerTileEntity(TilePipeTransport.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_transport"));
		GameRegistry.registerTileEntity(TilePipeModularMK1.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_modular_mk1"));
		GameRegistry.registerTileEntity(TilePipeModularMK2.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_modular_mk2"));
		GameRegistry.registerTileEntity(TilePipeModularMK3.class, new ResourceLocation(ModularPipes.MOD_ID, "pipe_modular_mk3"));
		GameRegistry.registerTileEntity(TileTank.class, new ResourceLocation(ModularPipes.MOD_ID, "tank"));
		GameRegistry.registerTileEntity(TileModularStoragePart.class, new ResourceLocation(ModularPipes.MOD_ID, "modular_storage_part"));
		GameRegistry.registerTileEntity(TileModularStorageCore.class, new ResourceLocation(ModularPipes.MOD_ID, "modular_storage_core"));
		GameRegistry.registerTileEntity(TileModularTankPart.class, new ResourceLocation(ModularPipes.MOD_ID, "modular_tank_part"));
		GameRegistry.registerTileEntity(TileModularTankCore.class, new ResourceLocation(ModularPipes.MOD_ID, "modular_tank_core"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_TRANSPORT).setRegistryName("pipe_transport"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK1).setRegistryName("pipe_modular_mk1"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK2).setRegistryName("pipe_modular_mk2"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK3).setRegistryName("pipe_modular_mk3"));
		r.register(new ItemBlockTank(ModularPipesBlocks.TANK).setRegistryName("tank"));
		r.register(new ItemBlock(ModularPipesBlocks.MODULAR_STORAGE).setRegistryName("modular_storage"));
		r.register(new ItemBlock(ModularPipesBlocks.MODULAR_TANK).setRegistryName("modular_tank"));

		r.register(withName(new ItemPainter(), "painter"));
		r.register(withName(new Item(), "module"));
		r.register(withName(new ItemModule(ModuleItemStorage::new), "module_item_storage"));
		r.register(withName(new ItemModule(ModuleItemHandler::new), "module_item_base"));
		r.register(withName(new ItemModule(ModuleItemExtract::new), "module_item_extract"));
		r.register(withName(new ItemModule(ModuleItemInsert::new), "module_item_insert"));
		r.register(withName(new ItemModule(ModuleFluidStorage::new), "module_fluid_storage"));
		r.register(withName(new ItemModule(ModuleFluidHandler::new), "module_fluid_base"));
		r.register(withName(new ItemModule(ModuleFluidExtract::new), "module_fluid_extract"));
		r.register(withName(new ItemModule(ModuleFluidInsert::new), "module_fluid_insert"));
		r.register(withName(new ItemModule(ModuleCrafting::new), "module_crafting"));
		r.register(withName(new ItemModule(ModuleEnergyInput::new), "module_energy_input"));
		r.register(withName(new ItemModule(ModuleEnergyOutput::new), "module_energy_output"));
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