package com.latmod.modularpipes.item.module;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class ModuleRightClickExtract extends ModuleExtract
{
	@Override
	public void updateModule()
	{
	}

	@Override
	public boolean onModuleRightClick(EntityPlayer player, EnumHand hand)
	{
		extractItem();
		return true;
	}
}