package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author LatvianModder
 */
@ObjectHolder(ModularPipes.MOD_ID)
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesItems {
	public static final Item TRANSPORT_PIPE = Items.AIR;
	public static final Item MODULAR_PIPE_MK1 = Items.AIR;
	public static final Item MODULAR_PIPE_MK2 = Items.AIR;
	public static final Item MODULAR_PIPE_MK3 = Items.AIR;

	public static final Item PAINTER = Items.AIR;
	public static final Item MODULE = Items.AIR;
	public static final Item INPUT_PART = Items.AIR;
	public static final Item OUTPUT_PART = Items.AIR;
	public static final Item ITEM_BASE_MODULE = Items.AIR;
	public static final Item ITEM_STORAGE_MODULE = Items.AIR;
	public static final Item ITEM_EXTRACT_MODULE = Items.AIR;
	public static final Item ITEM_INSERT_MODULE = Items.AIR;
	public static final Item FLUID_BASE_MODULE = Items.AIR;
	public static final Item FLUID_STORAGE_MODULE = Items.AIR;
	public static final Item FLUID_EXTRACT_MODULE = Items.AIR;
	public static final Item FLUID_INSERT_MODULE = Items.AIR;
	public static final Item CRAFTING_MODULE = Items.AIR;
	public static final Item ENERGY_INPUT_MODULE = Items.AIR;
	public static final Item ENERGY_OUTPUT_MODULE = Items.AIR;
}