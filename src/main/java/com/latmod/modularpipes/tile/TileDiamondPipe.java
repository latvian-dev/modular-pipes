package com.latmod.modularpipes.tile;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.block.ModularPipesBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TileDiamondPipe extends TileBase implements ITickable, IWorldNameable
{
	public final DiamondPipeInventory[] inventories = new DiamondPipeInventory[6];
	private boolean isDirty = false;
	private boolean shouldMoveItems = false;

	public TileDiamondPipe()
	{
		for (int i = 0; i < 6; i++)
		{
			inventories[i] = new DiamondPipeInventory(this, EnumFacing.VALUES[i]);
		}
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		NBTTagList configList = new NBTTagList();

		for (int i = 0; i < 6; i++)
		{
			NBTTagCompound nbt1 = inventories[i].filter.isEmpty() ? new NBTTagCompound() : inventories[i].filter.serializeNBT();

			if (inventories[i].mode != EnumDiamondPipeMode.OUT)
			{
				nbt1.setByte("mode", (byte) inventories[i].mode.ordinal());
			}

			configList.appendTag(nbt1);
		}

		nbt.setTag("config", configList);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		NBTTagList configList = nbt.getTagList("config", Constants.NBT.TAG_COMPOUND);

		if (configList.tagCount() == 6)
		{
			for (int i = 0; i < 6; i++)
			{
				NBTTagCompound nbt1 = configList.getCompoundTagAt(i);
				inventories[i].filter = new ItemStack(nbt1);

				if (inventories[i].filter.isEmpty())
				{
					inventories[i].filter = ItemStack.EMPTY;
				}

				inventories[i].mode = EnumDiamondPipeMode.VALUES[MathHelper.clamp(nbt1.getByte("mode"), 0, EnumDiamondPipeMode.VALUES.length)];
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null && inventories[side.getIndex()].mode != EnumDiamondPipeMode.DISABLED || super.hasCapability(capability, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null && inventories[side.getIndex()].mode != EnumDiamondPipeMode.DISABLED)
		{
			return (T) inventories[side.getIndex()];
		}

		return super.getCapability(capability, side);
	}

	@Override
	public void update()
	{
		if (!world.isRemote && (world.getTotalWorldTime() & 7L) == (hashCode() & 7L))
		{
			moveItems();
		}

		if (!world.isRemote && shouldMoveItems)
		{
			IItemHandler[] inventories = new IItemHandler[6];

			for (int i = 0; i < 6; i++)
			{
				BlockPos offset = pos.offset(EnumFacing.VALUES[i]);
				TileEntity tileEntity = world.isBlockLoaded(offset) ? world.getTileEntity(offset) : null;
				inventories[i] = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());
			}

			moveItemsOut(inventories);
			moveItemsIn(inventories);
			shouldMoveItems = false;
		}

		if (isDirty)
		{
			super.markDirty();
			isDirty = false;
		}
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	public void moveItems()
	{
		shouldMoveItems = true;
	}

	private void moveItemsOut(IItemHandler[] invs)
	{
		for (int i = 0; i < 6; i++)
		{
			if (invs[i] != null && inventories[i].mode == EnumDiamondPipeMode.OUT && !inventories[i].filter.isEmpty())
			{
				if (transferItems(inventories[i], invs[i], inventories[i].filter))
				{
					return;
				}
			}
		}

		for (int i = 0; i < 6; i++)
		{
			if (invs[i] != null && inventories[i].mode == EnumDiamondPipeMode.OUT && inventories[i].filter.isEmpty())
			{
				if (transferItems(inventories[i], invs[i], ItemStack.EMPTY))
				{
					return;
				}
			}
		}
	}

	private void moveItemsIn(IItemHandler[] invs)
	{
		for (int i = 0; i < 6; i++)
		{
			if (invs[i] != null && inventories[i].mode == EnumDiamondPipeMode.IN && !inventories[i].filter.isEmpty())
			{
				if (transferItems(invs[i], inventories[i], inventories[i].filter))
				{
					return;
				}
			}
		}

		for (int i = 0; i < 6; i++)
		{
			if (invs[i] != null && inventories[i].mode == EnumDiamondPipeMode.IN && inventories[i].filter.isEmpty())
			{
				if (transferItems(invs[i], inventories[i], ItemStack.EMPTY))
				{
					return;
				}
			}
		}
	}

	private boolean transferItems(@Nullable IItemHandler from, @Nullable IItemHandler to, ItemStack filter)
	{
		if (from == null || to == null || from.getSlots() == 0 || to.getSlots() == 0)
		{
			return false;
		}

		for (int slot = 0; slot < from.getSlots(); slot++)
		{
			ItemStack stack = from.extractItem(slot, 64, true);

			if (!stack.isEmpty() && (filter.isEmpty() || ItemFiltersAPI.filter(filter, stack)))
			{
				ItemStack stack1 = ItemHandlerHelper.insertItem(to, stack, true);

				if (!ItemFiltersAPI.areItemStacksEqual(stack, stack1))
				{
					ItemHandlerHelper.insertItem(to, stack, false);
					from.extractItem(slot, stack.getCount(), false);
					return true;
				}
			}
		}

		return false;
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