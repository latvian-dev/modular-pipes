package com.latmod.modularpipes.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class TilePipeTransport extends TilePipeBase
{
	public final TransportPipeInventory[] inventories = new TransportPipeInventory[6];

	public TilePipeTransport()
	{
		for (int i = 0; i < 6; i++)
		{
			inventories[i] = new TransportPipeInventory(this, EnumFacing.VALUES[i]);
		}
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		for (int i = 0; i < inventories.length; i++)
		{
			if (inventories[i].counter > 0)
			{
				nbt.setByte("counter_" + i, (byte) inventories[i].counter);
			}
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);

		for (int i = 0; i < inventories.length; i++)
		{
			inventories[i].counter = nbt.getByte("counter_" + i);
		}
	}

	@Override
	public boolean hasPipeItemHandler(EnumFacing side)
	{
		return true;
	}

	@Override
	public IPipeItemHandler getPipeItemHandler(EnumFacing side)
	{
		return inventories[side.getIndex()];
	}
}