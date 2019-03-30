package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.item.ItemPainter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ContainerPainter extends Container
{
	public final EntityPlayer player;
	public final ItemStack stack;

	public ContainerPainter(EntityPlayer p, ItemStack is)
	{
		player = p;
		stack = is;

		for (int k = 0; k < 3; ++k)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 36 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l)
		{
			if (player.inventory.getStackInSlot(l) != stack)
			{
				addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 94));
			}
			else
			{
				addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 94)
				{
					@Override
					public boolean canTakeStack(EntityPlayer player)
					{
						return false;
					}
				});
			}
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
		ItemPainter.setPaint(stack, inventorySlots.get(index).getStack());
		detectAndSendChanges();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int id)
	{
		return ItemPainter.setPaint(stack, player.inventory.getItemStack());
	}
}