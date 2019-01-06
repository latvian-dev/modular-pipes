package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class DiamondPipeInventory implements IPipeItemHandler
{
	public final TilePipeDiamond pipe;
	public final EnumFacing facing;
	public ItemStack filter = ItemStack.EMPTY;

	public DiamondPipeInventory(TilePipeDiamond p, EnumFacing f)
	{
		pipe = p;
		facing = f;
	}

	@Override
	public TilePipeBase getPipe()
	{
		return pipe;
	}

	@Override
	public EnumFacing getFacing()
	{
		return facing;
	}

	@Override
	public int getDirection(PipeItem item)
	{
		int[] dirs = new int[6];
		int pos = 0;

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && !pipe.inventories[i].filter.isEmpty() && pipe.inventories[i].filter.getItem() != ItemFiltersAPI.NULL_ITEM && ItemFiltersAPI.filter(pipe.inventories[i].filter, item.stack))
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));
				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler != null && (handler instanceof IPipeItemHandler || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		if (pos > 0)
		{
			return dirs[pipe.getWorld().rand.nextInt(pos)];
		}

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && pipe.inventories[i].filter.isEmpty())
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));
				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler != null && (handler instanceof IPipeItemHandler || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		return pos == 0 ? facing.getIndex() : dirs[pipe.getWorld().rand.nextInt(pos)];
	}
}