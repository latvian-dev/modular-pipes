package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.block.EnumMK;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK1 extends TilePipeBase
{
	public final ModularPipeInventory[] inventories = new ModularPipeInventory[6];

	public TilePipeModularMK1()
	{
		for (int i = 0; i < 6; i++)
		{
			inventories[i] = new ModularPipeInventory(this, EnumFacing.VALUES[i]);
		}
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);
		NBTTagList configList = new NBTTagList();

		for (int i = 0; i < 6; i++)
		{
			configList.appendTag(inventories[i].module.isEmpty() ? new NBTTagCompound() : inventories[i].module.serializeNBT());
		}

		nbt.setTag("config", configList);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);

		for (ModularPipeInventory inventory : inventories)
		{
			inventory.module = ItemStack.EMPTY;
		}

		NBTTagList configList = nbt.getTagList("config", Constants.NBT.TAG_COMPOUND);

		if (configList.tagCount() == 6)
		{
			for (int i = 0; i < 6; i++)
			{
				NBTTagCompound nbt1 = configList.getCompoundTagAt(i);
				ItemStack stack = new ItemStack(nbt1);

				if (!stack.isEmpty())
				{
					inventories[i].module = stack;
				}
			}
		}
	}

	@Override
	public boolean hasPipeItemHandler(EnumFacing side)
	{
		return inventories[side.getIndex()].module.getItem() != ItemFiltersAPI.NULL_ITEM;
	}

	@Override
	public IPipeItemHandler getPipeItemHandler(EnumFacing side)
	{
		return inventories[side.getIndex()].module.getItem() == ItemFiltersAPI.NULL_ITEM ? null : inventories[side.getIndex()];
	}

	public EnumMK getMK()
	{
		return EnumMK.MK1;
	}

	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
	}
}