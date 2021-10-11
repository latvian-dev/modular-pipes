package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.block.EnumMK;
import dev.latvian.mods.modularpipes.block.ModularPipeBlock;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.block.TransportPipeBlock;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK1BlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK2BlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK3BlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import dev.latvian.mods.modularpipes.block.entity.TransportPipeBlockEntity;
import dev.latvian.mods.modularpipes.item.ModuleItem;
import dev.latvian.mods.modularpipes.item.PipeBlockItem;
import dev.latvian.mods.modularpipes.item.module.CraftingModule;
import dev.latvian.mods.modularpipes.item.module.energy.ModuleEnergyInput;
import dev.latvian.mods.modularpipes.item.module.energy.ModuleEnergyOutput;
import dev.latvian.mods.modularpipes.item.module.fluid.FluidExtractModule;
import dev.latvian.mods.modularpipes.item.module.fluid.FluidHandlerModule;
import dev.latvian.mods.modularpipes.item.module.fluid.FluidInsertModule;
import dev.latvian.mods.modularpipes.item.module.fluid.FluidStorageModule;
import dev.latvian.mods.modularpipes.item.module.item.ItemExtractModule;
import dev.latvian.mods.modularpipes.item.module.item.ItemHandlerModule;
import dev.latvian.mods.modularpipes.item.module.item.ItemInsertModule;
import dev.latvian.mods.modularpipes.item.module.item.ItemStorageModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesEventHandler {
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	private static Block withName(Block block, String name) {
		block.setRegistryName(name);
		return block;
	}

	private static Item.Properties ip() {
		return new Item.Properties().tab(ModularPipes.TAB);
	}

	private static Item withName(Item item, String name) {
		item.setRegistryName(name);
		return item;
	}

	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		r.register(withName(new TransportPipeBlock(), "transport_pipe"));
		r.register(withName(new ModularPipeBlock(EnumMK.MK1), "modular_pipe_mk1"));
		r.register(withName(new ModularPipeBlock(EnumMK.MK2), "modular_pipe_mk2"));
		r.register(withName(new ModularPipeBlock(EnumMK.MK3), "modular_pipe_mk3"));

	}

	public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
		IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
		r.register(create(ModularPipesBlocks.TRANSPORT_PIPE, TransportPipeBlockEntity::new));
		r.register(create(ModularPipesBlocks.MODULAR_PIPE_MK1, ModularPipeMK1BlockEntity::new));
		r.register(create(ModularPipesBlocks.MODULAR_PIPE_MK2, ModularPipeMK2BlockEntity::new));
		r.register(create(ModularPipesBlocks.MODULAR_PIPE_MK3, ModularPipeMK3BlockEntity::new));
	}

	public static BlockEntityType<?> create(Block block, Supplier<BlockEntity> t) {
		return BlockEntityType.Builder.of(t, block).build(null).setRegistryName(block.getRegistryName());
	}

	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(new PipeBlockItem(ModularPipesBlocks.TRANSPORT_PIPE, ip()).setRegistryName("transport_pipe"));
		r.register(new PipeBlockItem(ModularPipesBlocks.MODULAR_PIPE_MK1, ip()).setRegistryName("modular_pipe_mk1"));
		r.register(new PipeBlockItem(ModularPipesBlocks.MODULAR_PIPE_MK2, ip()).setRegistryName("modular_pipe_mk2"));
		r.register(new PipeBlockItem(ModularPipesBlocks.MODULAR_PIPE_MK3, ip()).setRegistryName("modular_pipe_mk3"));

		r.register(withName(new Item(ip()), "module"));
		r.register(withName(new Item(ip()), "input_part"));
		r.register(withName(new Item(ip()), "output_part"));
		r.register(withName(new ModuleItem(ItemStorageModule::new, ip()), "item_storage_module"));
		r.register(withName(new ModuleItem(ItemHandlerModule::new, ip()), "item_base_module"));
		r.register(withName(new ModuleItem(ItemExtractModule::new, ip()), "item_extract_module"));
		r.register(withName(new ModuleItem(ItemInsertModule::new, ip()), "item_insert_module"));
		r.register(withName(new ModuleItem(FluidStorageModule::new, ip()), "fluid_storage_module"));
		r.register(withName(new ModuleItem(FluidHandlerModule::new, ip()), "fluid_base_module"));
		r.register(withName(new ModuleItem(FluidExtractModule::new, ip()), "fluid_extract_module"));
		r.register(withName(new ModuleItem(FluidInsertModule::new, ip()), "fluid_insert_module"));
		r.register(withName(new ModuleItem(CraftingModule::new, ip()), "crafting_module"));
		r.register(withName(new ModuleItem(ModuleEnergyInput::new, ip()), "energy_input_module"));
		r.register(withName(new ModuleItem(ModuleEnergyOutput::new, ip()), "energy_output_module"));
	}

	@SubscribeEvent
	public static void attachLevelCap(AttachCapabilitiesEvent<Level> event) {
		event.addCapability(WORLD_CAP_ID, new PipeNetwork(event.getObject()));
	}

	@SubscribeEvent
	public static void tickServerWorld(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			PipeNetwork network = PipeNetwork.get(event.world);

			if (network != null) {
				network.tick();
			}
		}
	}
}