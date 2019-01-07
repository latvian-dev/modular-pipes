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
public class ModularPipeInventory implements IPipeItemHandler
{
	public final TilePipeModularMK1 pipe;
	public final EnumFacing facing;
	public ItemStack module = ItemStack.EMPTY;

	public ModularPipeInventory(TilePipeModularMK1 p, EnumFacing f)
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
		return IPipeItemHandler.insertPipeItem(this, item, getDirection(item), simulate);
	}

	private int getDirection(PipeItem item)
	{
		int[] dirs = new int[6];
		int pos = 0;

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && !pipe.inventories[i].module.isEmpty() && pipe.inventories[i].module.getItem() != ItemFiltersAPI.NULL_ITEM && ItemFiltersAPI.filter(pipe.inventories[i].module, item.stack))
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));

				if (tileEntity instanceof TilePipeTransport && !pipe.canPipesConnect(((TilePipeTransport) tileEntity).skin))
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

		if (pos > 0)
		{
			return dirs[pipe.getWorld().rand.nextInt(pos)];
		}

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && pipe.inventories[i].module.isEmpty())
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));

				if (tileEntity instanceof TilePipeTransport && !pipe.canPipesConnect(((TilePipeTransport) tileEntity).skin))
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

		return pos == 0 ? facing.getIndex() : dirs[pipe.getWorld().rand.nextInt(pos)];
	}
}