package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.block.ModularPipesBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class TilePipeDiamond extends TilePipeBase implements IWorldNameable
{
	public final DiamondPipeInventory[] inventories = new DiamondPipeInventory[6];

	public TilePipeDiamond()
	{
		for (int i = 0; i < 6; i++)
		{
			inventories[i] = new DiamondPipeInventory(this, EnumFacing.VALUES[i]);
		}
	}

	public boolean hasFilters()
	{
		for (DiamondPipeInventory inventory : inventories)
		{
			if (!inventory.filter.isEmpty())
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (hasFilters())
		{
			NBTTagList configList = new NBTTagList();

			for (int i = 0; i < 6; i++)
			{
				configList.appendTag(inventories[i].filter.isEmpty() ? new NBTTagCompound() : inventories[i].filter.serializeNBT());
			}

			nbt.setTag("config", configList);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);

		for (DiamondPipeInventory inventory : inventories)
		{
			inventory.filter = ItemStack.EMPTY;
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
					inventories[i].filter = stack;
				}
			}
		}
	}

	@Override
	public boolean hasPipeItemHandler(EnumFacing side)
	{
		return inventories[side.getIndex()].filter.getItem() != ItemFiltersAPI.NULL_ITEM;
	}

	@Override
	public IPipeItemHandler getPipeItemHandler(EnumFacing side)
	{
		return inventories[side.getIndex()].filter.getItem() == ItemFiltersAPI.NULL_ITEM ? null : inventories[side.getIndex()];
	}

	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
		float pipeSpeed = 0.25F;

		if (item.speed < pipeSpeed)
		{
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
	}

	@Override
	public String getName()
	{
		return ModularPipesBlocks.PIPE_DIAMOND.getTranslationKey() + ".name";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentTranslation(getName());
	}
}