package com.latmod.mods.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.item.module.SidedPipeModule;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModuleItemHandler extends SidedPipeModule implements IItemHandler
{
	public ItemStack filter = ItemStack.EMPTY;
	private List<ModuleItemStorage> storageModules = null;

	@Override
	public void writeData(CompoundNBT nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.put("filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(CompoundNBT nbt)
	{
		super.readData(nbt);
		filter = ItemStack.read(nbt.getCompound("filter"));

		if (filter.isEmpty())
		{
			filter = ItemStack.EMPTY;
		}
	}

	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == side ? thisOptional.cast() : super.getCapability(capability, facing);
	}

	public List<ModuleItemStorage> getStorageModules()
	{
		if (storageModules == null)
		{
			storageModules = new ArrayList<>(2);

			for (TilePipeModularMK1 pipe1 : pipe.getPipeNetwork())
			{
				for (PipeModule module : pipe1.modules)
				{
					if (module instanceof ModuleItemStorage)
					{
						storageModules.add((ModuleItemStorage) module);
					}
				}
			}

			if (storageModules.size() > 1)
			{
				storageModules.sort(ModuleItemStorage.COMPARATOR);
			}
		}

		return storageModules;
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		storageModules = null;
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
		for (ModuleItemStorage module : getStorageModules())
		{
			if (ItemFiltersAPI.filter(module.filter, stack))
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
		}

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

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack)
	{
		for (ModuleItemStorage module : getStorageModules())
		{
			if (ItemFiltersAPI.filter(module.filter, stack))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onModuleRightClick(PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.isEmpty())
		{
			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new StringTextComponent("Filter: " + filter.getDisplayName()), true); //LANG
			}
		}
		else
		{
			filter = ItemHandlerHelper.copyStackWithSize(stack, 1);

			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new StringTextComponent("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}