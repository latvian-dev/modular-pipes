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
		item.speed = 0.2F;

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

	boolean insertPipeItem(PipeItem item, boolean simulate);

	static boolean insertPipeItem(IPipeItemHandler pipe, PipeItem item, int to, boolean simulate)
	{
		if (simulate)
		{
			return true;
		}

		item.from = pipe.getFacing().getIndex();
		item.to = to;
		item.lifespan = item.stack.getItem().getEntityLifespan(item.stack, pipe.getPipe().getWorld());
		pipe.getPipe().items.add(item);
		pipe.getPipe().markDirty();
		pipe.getPipe().sync = false;
		return true;
	}
}