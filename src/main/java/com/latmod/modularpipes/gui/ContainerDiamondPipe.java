package com.latmod.modularpipes.gui;

import com.latmod.modularpipes.tile.EnumDiamondPipeMode;
import com.latmod.modularpipes.tile.TileDiamondPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class ContainerDiamondPipe extends Container
{
	public final EntityPlayer player;
	public final TileDiamondPipe pipe;

	public ContainerDiamondPipe(EntityPlayer ep, TileDiamondPipe p)
	{
		player = ep;
		pipe = p;

		for (int k = 0; k < 3; ++k)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l)
		{
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 142));
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
			pipe.inventories[id].mode = EnumDiamondPipeMode.VALUES[(pipe.inventories[id].mode.ordinal() + 1) % EnumDiamondPipeMode.VALUES.length];
			pipe.markDirty();
			IBlockState state = pipe.getWorld().getBlockState(pipe.getPos());
			pipe.getWorld().notifyBlockUpdate(pipe.getPos(), state, state, 11);
			return true;
		}
		else if (id >= 6 && id <= 11)
		{
			pipe.inventories[id - 6].filter = player.inventory.getItemStack().isEmpty() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(player.inventory.getItemStack(), 1);
			pipe.markDirty();
			return true;
		}

		return false;
	}
}