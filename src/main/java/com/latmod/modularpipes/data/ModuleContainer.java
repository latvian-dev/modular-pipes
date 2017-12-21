package com.latmod.modularpipes.data;

import com.feed_the_beast.ftblib.lib.tile.EnumSaveType;
import com.feed_the_beast.ftblib.lib.util.misc.DataStorage;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public final class ModuleContainer implements ITickable, IItemHandler
{
	public final TileModularPipe tile;
	public final EnumFacing facing;
	private Module module;
	private ItemStack stack;
	private DataStorage data;
	private FilterConfig filterConfig;
	private int tick;

	public ModuleContainer(TileModularPipe t, EnumFacing f, ItemStack stack)
	{
		tile = t;
		facing = f;
		filterConfig = new FilterConfig();
		setStack(stack);
	}

	public ModuleContainer(TileModularPipe t, NBTTagCompound nbt, EnumSaveType type)
	{
		this(t, EnumFacing.VALUES[nbt.getByte("Facing")], ItemStack.EMPTY);

		if (nbt.hasKey("Item"))
		{
			setStack(new ItemStack(nbt.getCompoundTag("Item")));
			data.deserializeNBT(nbt.getCompoundTag("Data"), type);
		}

		if (nbt.hasKey("FilterConfig"))
		{
			filterConfig.deserializeNBT(nbt.getTag("FilterConfig"));
		}

		tick = nbt.getInteger("Tick");
	}

	public PipeNetwork getNetwork()
	{
		return tile.getNetwork();
	}

	public boolean hasModule()
	{
		return !getModule().isEmptyModule();
	}

	public void setStack(ItemStack is)
	{
		module = Module.EMPTY;
		data = DataStorage.EMPTY;
		stack = ItemStack.EMPTY;
		tick = 0;

		if (is.getItem() instanceof Module)
		{
			stack = is;
			module = (Module) is.getItem();

			if (!module.isEmptyModule())
			{
				data = module.createModuleData(this);
				filterConfig = module.createFilterConfig(this);
			}
		}
	}

	public Module getModule()
	{
		return module;
	}

	public ItemStack getItemStack()
	{
		return stack;
	}

	public DataStorage getData()
	{
		return data;
	}

	public FilterConfig getFilterConfig()
	{
		return filterConfig;
	}

	public int getTick()
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
		return tile.getWorld().getTileEntity(tile.getPos().offset(facing));
	}

	public boolean isRemote()
	{
		return tile.getWorld().isRemote;
	}

	public NBTTagCompound writeToNBT(EnumSaveType type)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("Facing", (byte) facing.getIndex());

		if (tick > 0)
		{
			nbt.setInteger("Tick", tick);
		}
		if (filterConfig.shouldSave())
		{
			nbt.setTag("FilterConfig", filterConfig.serializeNBT());
		}
		if (!stack.isEmpty())
		{
			nbt.setTag("Item", stack.serializeNBT());
			NBTTagCompound nbt1 = new NBTTagCompound();
			data.serializeNBT(nbt1, type);

			if (!nbt1.hasNoTags())
			{
				nbt.setTag("Data", nbt1);
			}
		}

		return nbt;
	}

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Nonnull
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