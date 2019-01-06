package com.latmod.modularpipes.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public interface IPipeItemHandler extends IItemHandler
{
	TilePipeBase getPipe();

	EnumFacing getFacing();

	@Override
	default int getSlots()
	{
		return 1;
	}

	@Override
	default ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	default ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		PipeItem item = new PipeItem();
		item.stack = stack;
		item.speed = 0.1F;

		if (insertPipeItem(item, simulate))
		{
			if (!simulate)
			{
				getPipe().markDirty();
			}

			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	default ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	default int getSlotLimit(int slot)
	{
		return 64;
	}

	default boolean insertPipeItem(PipeItem item, boolean simulate)
	{
		int to = getDirection(item);

		if (to == 6)
		{
			return false;
		}

		if (simulate)
		{
			return true;
		}

		item.from = getFacing().getIndex();
		item.to = to;
		item.lifespan = item.stack.getItem().getEntityLifespan(item.stack, getPipe().getWorld());
		getPipe().items.add(item);
		getPipe().markDirty();
		getPipe().sync = false;
		return true;
	}

	default int getDirection(PipeItem item)
	{
		return 6;
	}
}