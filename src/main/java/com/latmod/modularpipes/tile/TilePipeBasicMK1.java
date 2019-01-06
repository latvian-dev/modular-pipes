package com.latmod.modularpipes.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class TilePipeBasicMK1 extends TilePipeBase
{
	public final BasicPipeInventory[] inventories = new BasicPipeInventory[6];
	public int counter = 0;

	public TilePipeBasicMK1()
	{
		for (int i = 0; i < 6; i++)
		{
			inventories[i] = new BasicPipeInventory(this, EnumFacing.VALUES[i]);
		}
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (counter > 0)
		{
			nbt.setShort("counter", (short) counter);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		counter = nbt.getShort("counter");
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

	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
		float pipeSpeed = 0.05F;

		if (item.speed > pipeSpeed)
		{
			item.speed *= 0.98F;

			if (item.speed < pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
		else if (item.speed < pipeSpeed)
		{
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
	}
}