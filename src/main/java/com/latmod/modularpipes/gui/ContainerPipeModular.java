package com.latmod.modularpipes.gui;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

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
		if (id >= 0 && id <= 5)
		{
			if (player.inventory.getItemStack().isEmpty())
			{
				if (pipe.inventories[id].module.isEmpty())
				{
					pipe.inventories[id].module = new ItemStack(ItemFiltersAPI.NULL_ITEM);
				}
				else
				{
					pipe.inventories[id].module = ItemStack.EMPTY;
				}
			}
			else
			{
				pipe.inventories[id].module = ItemHandlerHelper.copyStackWithSize(player.inventory.getItemStack(), 1);
			}

			pipe.markDirty();

			if (player.world.isRemote)
			{
				IBlockState state = player.world.getBlockState(pipe.getPos());
				player.world.notifyBlockUpdate(pipe.getPos(), state, state, 11);
			}

			return true;
		}

		return false;
	}
}