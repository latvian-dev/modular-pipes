package dev.latvian.mods.modularpipes;

import net.minecraft.world.item.ItemStack;

public class FilterUtils {
	public static boolean check(ItemStack filter, ItemStack stack) {
		// return ItemFiltersAPI.filter(filter, stack);
		return filter.getItem() == stack.getItem();
	}
}
