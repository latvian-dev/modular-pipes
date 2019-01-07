package com.latmod.modularpipes.data;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public final class ModuleContainer implements ITickable, IItemHandler
{
	public final TilePipeModularMK1 tile;
	public final EnumFacing facing;
	private IModule module;
	private ItemStack stack;
	private long tick;

	public ModuleContainer(TilePipeModularMK1 t, EnumFacing f, ItemStack stack)
	{
		tile = t;
		facing = f;
		setStack(stack);
	}

	public ModuleContainer(TilePipeModularMK1 t, NBTTagCompound nbt)
	{
		this(t, EnumFacing.VALUES[nbt.getByte("Facing")], ItemStack.EMPTY);

		if (nbt.hasKey("Item"))
		{
			setStack(new ItemStack(nbt.getCompoundTag("Item")));
		}

		tick = nbt.getLong("Tick");
	}

	public boolean hasModule()
	{
		return module != null;
	}

	public void setStack(ItemStack is)
	{
		stack = is.isEmpty() ? ItemStack.EMPTY : is;
		module = is.getCapability(ModularPipes.MODULE_CAP, null);
		tick = 0L;
	}

	@Nullable
	public IModule getModule()
	{
		return module;
	}

	public ItemStack getItemStack()
	{
		return stack;
	}

	public long getTick()
	{
		return tick;
	}

	@Override
	public void update()
	{
		if (hasModule())
		{
			module.updateModule(this);
			tick++;
		}
	}

	@Nullable
	public TileEntity getFacingTile()
	{
		if (!tile.hasWorld())
		{
			return null;
		}

		BlockPos pos = tile.getPos().offset(facing);
		return tile.getWorld().isBlockLoaded(pos) ? tile.getWorld().getTileEntity(pos) : null;
	}

	public boolean isRemote()
	{
		return tile.getWorld().isRemote;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("Facing", (byte) facing.getIndex());

		if (tick > 0L)
		{
			nbt.setLong("Tick", tick);
		}

		if (!stack.isEmpty())
		{
			nbt.setTag("Item", stack.serializeNBT());
		}

		return nbt;
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
}