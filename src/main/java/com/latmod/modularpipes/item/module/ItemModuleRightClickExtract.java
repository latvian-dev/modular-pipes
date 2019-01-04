package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.data.ModuleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class ItemModuleRightClickExtract extends ItemModuleExtract
{
	@Override
	public void updateModule(ModuleContainer container)
	{
	}

	@Override
	public boolean onModuleRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
	{
		extractItem(container);
		return true;
	}
}