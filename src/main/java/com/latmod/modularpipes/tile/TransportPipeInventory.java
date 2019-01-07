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
public class TransportPipeInventory implements IPipeItemHandler
{
	private final TilePipeTransport pipe;
	private final EnumFacing facing;
	public int counter = 0;

	public TransportPipeInventory(TilePipeTransport p, EnumFacing f)
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
	public boolean insertPipeItem(PipeItem item, boolean simulate)
	{
		int[] dirs = new int[6];
		int pos = 0;

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex())
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));

				if (tileEntity instanceof TilePipeTransport && !pipe.canColorConnect(((TilePipeTransport) tileEntity).color))
				{
					continue;
				}

				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler != null && (handler instanceof IPipeItemHandler || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		int to = pos == 0 ? facing.getIndex() : dirs[counter % pos];

		if (IPipeItemHandler.insertPipeItem(this, item, to, simulate))
		{
			if (!simulate && pos > 1)
			{
				counter++;
				counter %= pos;
			}

			return true;
		}

		return false;
	}
}