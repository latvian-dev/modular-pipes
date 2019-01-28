package com.latmod.modularpipes.item.module.item;

import com.latmod.modularpipes.item.ItemKey;
import com.latmod.modularpipes.item.module.SidePipeModule;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModuleItemHandler extends SidePipeModule implements IItemHandler
{
	public ItemStack filter = ItemStack.EMPTY;
	private Map<ItemKey, Optional<ModuleItemStorage>> lastDestinationInsert = null;
	private Map<ItemKey, Optional<ModuleItemStorage>> lastDestinationExtract = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = new ItemStack(nbt.getCompoundTag("filter"));

		if (filter.isEmpty())
		{
			filter = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == side || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == side ? (T) this : super.getCapability(capability, facing);
	}

	public List<ModuleItemStorage> getStorageModules(ItemStack stack)
	{
		/*
		if (storageModules == null)
		{
			storageModules = new HashMap<>();
		}

		ItemKey key = ItemKey.of(stack);
		List<ModuleItemStorage> list = storageModules.get(key);

		if (list == null)
		{
			list = new ArrayList<>(2);
			HashSet<TilePipeModularMK1> set = new HashSet<>();
			pipe.getNetwork(set);

			for (TilePipeModularMK1 pipe1 : set)
			{
				for (PipeModule module : pipe1.modules)
				{
					if (module instanceof ModuleItemStorage && (stack.isEmpty() || ItemFiltersAPI.filter(((ModuleItemStorage) module).filter, stack)))
					{
						list.add((ModuleItemStorage) module);
					}
				}
			}

			storageModules.put(key, list);
		}

		return list;
		*/
		return Collections.emptyList();
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		lastDestinationInsert = null;
		lastDestinationExtract = null;
	}

	@Override
	public int getSlots()
	{
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (slot == 1)
		{
			return stack;
		}

		List<ModuleItemStorage> list = getStorageModules(stack);

		if (list.size() > 1)
		{
			Object2IntOpenHashMap<ModuleItemStorage> map = new Object2IntOpenHashMap<>();

			for (ModuleItemStorage module : list)
			{
			}

			list = new ArrayList<>(list);

			list.sort((o1, o2) -> {


				return ModuleItemStorage.COMPARATOR.compare(o1, o2);
			});
		}

		for (ModuleItemStorage module : getStorageModules(stack))
		{
			IItemHandler handler1 = module.getItemHandler();

			if (handler1 != null)
			{
				stack = ItemHandlerHelper.insertItem(handler1, stack, simulate);

				if (stack.isEmpty())
				{
					return ItemStack.EMPTY;
				}
			}
		}

		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (slot == 0)
		{
			return ItemStack.EMPTY;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean onModuleRightClick(EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.isEmpty())
		{
			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new TextComponentString("Filter: " + filter.getDisplayName()), true); //LANG
			}
		}
		else
		{
			filter = ItemHandlerHelper.copyStackWithSize(stack, 1);

			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new TextComponentString("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}