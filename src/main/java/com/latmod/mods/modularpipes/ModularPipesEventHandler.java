package com.latmod.mods.modularpipes;

import com.latmod.mods.modularpipes.block.BlockModularStorage;
import com.latmod.mods.modularpipes.block.BlockModularTank;
import com.latmod.mods.modularpipes.block.BlockPipeModular;
import com.latmod.mods.modularpipes.block.BlockPipeTransport;
import com.latmod.mods.modularpipes.block.BlockTank;
import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.ModularPipesBlocks;
import com.latmod.mods.modularpipes.client.RenderTank;
import com.latmod.mods.modularpipes.gui.painter.ContainerPainter;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesEventHandler
{
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	private static Block withName(Block block, String name)
	{
		block.setRegistryName(name);
		return block;
	}

	private static Item.Properties ip()
	{
		return new Item.Properties().group(ModularPipes.TAB);
	}

	private static Item withName(Item item, String name)
	{
		item.setRegistryName(name);
		return item;
	}

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

	}

	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
//		event.getRegistry().register(new ContainerType<>((ContainerType.IFactory<Container>) (p_create_1_, p_create_2_) -> new ContainerPainter(p_create_1_, p_create_2_)));
	}
	public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event)
	{
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		r.register(create(ModularPipesBlocks.PIPE_TRANSPORT, TilePipeTransport::new));
		r.register(create(ModularPipesBlocks.PIPE_MODULAR_MK1, TilePipeModularMK1::new));
		r.register(create(ModularPipesBlocks.PIPE_MODULAR_MK2, TilePipeModularMK2::new));
		r.register(create(ModularPipesBlocks.PIPE_MODULAR_MK3, TilePipeModularMK3::new));
		r.register(create(ModularPipesBlocks.TANK, TileTank::new));
		r.register(create("modular_storage_part", TileModularStoragePart::new, ModularPipesBlocks.MODULAR_STORAGE));
		r.register(create("modular_storage_core", TileModularStorageCore::new, ModularPipesBlocks.MODULAR_STORAGE));
		r.register(create("modular_tank_part", TileModularTankPart::new, ModularPipesBlocks.MODULAR_TANK));
		r.register(create("modular_tank_core", TileModularTankCore::new, ModularPipesBlocks.MODULAR_TANK));
	}

	public static TileEntityType<?> create(Block block, Supplier<TileEntity> t)
	{
		return TileEntityType.Builder.create(t, block).build(null).setRegistryName(block.getRegistryName());
	}

	public static TileEntityType<?> create(String name, Supplier<TileEntity> t, Block... blocks)
	{
		return TileEntityType.Builder.create(t, blocks).build(null).setRegistryName(new ResourceLocation(ModularPipes.MOD_ID, name));
	}

	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_TRANSPORT, ip()).setRegistryName("pipe_transport"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK1, ip()).setRegistryName("pipe_modular_mk1"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK2, ip()).setRegistryName("pipe_modular_mk2"));
		r.register(new ItemBlockPipe(ModularPipesBlocks.PIPE_MODULAR_MK3, ip()).setRegistryName("pipe_modular_mk3"));
		r.register(new ItemBlockTank(ModularPipesBlocks.TANK, ip().setTEISR(() -> RenderTank.TankTEISR::new)).setRegistryName("tank"));
		r.register(new BlockItem(ModularPipesBlocks.MODULAR_STORAGE, ip()).setRegistryName("modular_storage"));
		r.register(new BlockItem(ModularPipesBlocks.MODULAR_TANK, ip()).setRegistryName("modular_tank"));

		r.register(withName(new ItemPainter(ip().maxStackSize(1)), "painter"));
		r.register(withName(new Item(ip()), "module"));
		r.register(withName(new Item(ip()), "input_part"));
		r.register(withName(new Item(ip()), "output_part"));
		r.register(withName(new ItemModule(ModuleItemStorage::new, ip()), "module_item_storage"));
		r.register(withName(new ItemModule(ModuleItemHandler::new, ip()), "module_item_base"));
		r.register(withName(new ItemModule(ModuleItemExtract::new, ip()), "module_item_extract"));
		r.register(withName(new ItemModule(ModuleItemInsert::new, ip()), "module_item_insert"));
		r.register(withName(new ItemModule(ModuleFluidStorage::new, ip()), "module_fluid_storage"));
		r.register(withName(new ItemModule(ModuleFluidHandler::new, ip()), "module_fluid_base"));
		r.register(withName(new ItemModule(ModuleFluidExtract::new, ip()), "module_fluid_extract"));
		r.register(withName(new ItemModule(ModuleFluidInsert::new, ip()), "module_fluid_insert"));
		r.register(withName(new ItemModule(ModuleCrafting::new, ip()), "module_crafting"));
		r.register(withName(new ItemModule(ModuleEnergyInput::new, ip()), "module_energy_input"));
		r.register(withName(new ItemModule(ModuleEnergyOutput::new, ip()), "module_energy_output"));
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
			PipeNetwork network = PipeNetwork.get(event.world);

			if (network != null)
			{
				network.tick();
			}
		}
	}
}