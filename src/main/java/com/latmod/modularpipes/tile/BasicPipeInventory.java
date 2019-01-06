package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class BasicPipeInventory implements IPipeItemHandler
{
	private final TilePipeBasicMK1 pipe;
	private final EnumFacing facing;

	public BasicPipeInventory(TilePipeBasicMK1 p, EnumFacing f)
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
			if (i != facing.getIndex())
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));
				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler instanceof BasicPipeInventory && pipe.getBlockType() != tileEntity.getBlockType())
				{
					continue;
				}

				if (handler != null && (handler instanceof IPipeItemHandler || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		return pos == 0 ? facing.getIndex() : dirs[pipe.counter % pos];
	}

	@Override
	public boolean insertPipeItem(PipeItem item, boolean simulate)
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
		pipe.items.add(item);
		pipe.markDirty();
		pipe.sync = false;
		pipe.counter++;

		if (pipe.counter > Short.MAX_VALUE)
		{
			pipe.counter = 0;
		}

		return true;
	}
}