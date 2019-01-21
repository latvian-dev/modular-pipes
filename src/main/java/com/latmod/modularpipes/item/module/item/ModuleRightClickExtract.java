package com.latmod.modularpipes.item.module.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class ModuleRightClickExtract extends ModuleExtract
{
	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public boolean onModuleRightClick(EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.isEmpty())
		{
			extractItem();
		}
		else
		{
			filter = ItemHandlerHelper.copyStackWithSize(stack, 1);

			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new TextComponentString("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}