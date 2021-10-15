package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public interface ModularPipesItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ModularPipes.MOD_ID);

	static Supplier<Item> basicItem(String id) {
		return REGISTRY.register(id, () -> new Item(new Item.Properties().tab(ModularPipes.TAB)));
	}

	static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup) {
		return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties().tab(ModularPipes.TAB)));
	}

	Supplier<BlockItem> TRANSPORT_PIPE = blockItem("transport_pipe", ModularPipesBlocks.TRANSPORT_PIPE);
	Supplier<BlockItem> MODULAR_PIPE_MK1 = blockItem("modular_pipe_mk1", ModularPipesBlocks.MODULAR_PIPE_MK1);
	Supplier<BlockItem> MODULAR_PIPE_MK2 = blockItem("modular_pipe_mk2", ModularPipesBlocks.MODULAR_PIPE_MK2);
	Supplier<BlockItem> MODULAR_PIPE_MK3 = blockItem("modular_pipe_mk3", ModularPipesBlocks.MODULAR_PIPE_MK3);

	Supplier<Item> MODULE = basicItem("module");
	Supplier<Item> INPUT_PART = basicItem("input_part");
	Supplier<Item> OUTPUT_PART = basicItem("output_part");
	Supplier<Item> ITEM_BASE_MODULE = REGISTRY.register("item_base_module", () -> new ModuleItem(ItemStorageModule::new));
	Supplier<Item> ITEM_STORAGE_MODULE = REGISTRY.register("item_storage_module", () -> new ModuleItem(ItemHandlerModule::new));
	Supplier<Item> ITEM_EXTRACT_MODULE = REGISTRY.register("item_extract_module", () -> new ModuleItem(ItemExtractModule::new));
	Supplier<Item> ITEM_INSERT_MODULE = REGISTRY.register("item_insert_module", () -> new ModuleItem(ItemInsertModule::new));
	Supplier<Item> FLUID_BASE_MODULE = REGISTRY.register("fluid_base_module", () -> new ModuleItem(FluidStorageModule::new));
	Supplier<Item> FLUID_STORAGE_MODULE = REGISTRY.register("fluid_storage_module", () -> new ModuleItem(FluidHandlerModule::new));
	Supplier<Item> FLUID_EXTRACT_MODULE = REGISTRY.register("fluid_extract_module", () -> new ModuleItem(FluidExtractModule::new));
	Supplier<Item> FLUID_INSERT_MODULE = REGISTRY.register("fluid_insert_module", () -> new ModuleItem(FluidInsertModule::new));
	Supplier<Item> CRAFTING_MODULE = REGISTRY.register("crafting_module", () -> new ModuleItem(CraftingModule::new));
	Supplier<Item> ENERGY_INPUT_MODULE = REGISTRY.register("energy_input_module", () -> new ModuleItem(ModuleEnergyInput::new));
	Supplier<Item> ENERGY_OUTPUT_MODULE = REGISTRY.register("energy_output_module", () -> new ModuleItem(ModuleEnergyOutput::new));
	Supplier<Item> LIGHT = basicItem("light");
	Supplier<Item> WRENCH = REGISTRY.register("wrench", WrenchItem::new);
}