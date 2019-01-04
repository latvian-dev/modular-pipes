package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class DiamondPipeInventory implements IItemHandler
{
	public final TileDiamondPipe pipe;
	public final EnumFacing facing;
	public ItemStack filter = ItemStack.EMPTY;
	public EnumDiamondPipeMode mode = EnumDiamondPipeMode.OUT;

	public DiamondPipeInventory(TileDiamondPipe p, EnumFacing f)
	{
		pipe = p;
		facing = f;
	}

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (mode == EnumDiamondPipeMode.OUT)
		{
			return stack;
		}

		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return ItemFiltersAPI.filter(filter, stack);
	}
}