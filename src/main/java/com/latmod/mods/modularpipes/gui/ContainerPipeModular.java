package com.latmod.mods.modularpipes.gui;

import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ContainerPipeModular extends Container
{
	public final EntityPlayer player;
	public final TilePipeModularMK1 pipe;
	public final int facing;

	public ContainerPipeModular(EntityPlayer ep, TilePipeModularMK1 p, int f)
	{
		player = ep;
		pipe = p;
		facing = f;

		for (int k = 0; k < 3; ++k)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 36 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l)
		{
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 94));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int id)
	{
		return false;
	}
}