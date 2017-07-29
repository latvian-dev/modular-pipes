package com.latmod.modularpipes.item;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.item.Item;

/**
 * @author LatvianModder
 */
public class ItemMPBase extends Item
{
	public ItemMPBase(String id)
	{
		setRegistryName(ModularPipes.MOD_ID + ':' + id);
		setUnlocalizedName(ModularPipes.MOD_ID + '.' + id);
		setCreativeTab(ModularPipes.TAB);
	}
}