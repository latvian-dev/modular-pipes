package com.latmod.modularpipes.item;

import com.feed_the_beast.ftblib.lib.item.ItemBase;
import com.latmod.modularpipes.ModularPipes;

/**
 * @author LatvianModder
 */
public class ItemMPBase extends ItemBase
{
	public ItemMPBase(String id)
	{
		super(ModularPipes.MOD_ID, id);
		setCreativeTab(ModularPipes.TAB);
	}
}