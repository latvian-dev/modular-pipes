package com.latmod.mods.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.modularpipes.ModularPipesCommon;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.item.module.SidedPipeModule;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModuleItemInsert extends SidedPipeModule
{
	public ItemStack filter = ItemStack.EMPTY;
	public int tick = 0;
	private List<ModuleItemStorage> storageModules = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (tick > 0)
		{
			nbt.setByte("tick", (byte) tick);
		}

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		tick = nbt.getByte("tick");

		filter = new ItemStack(nbt.getCompoundTag("filter"));

		if (filter.isEmpty())
		{
			filter = ItemStack.EMPTY;
		}
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
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateModule()
	{
		if (filter.isEmpty())
		{
			return;
		}

		tick++;

		if (tick >= 7)
		{
			if (insertItem())
			{
				spawnParticle(ModularPipesCommon.EXPLOSION);
			}

			tick = 0;
		}
	}

	private boolean insertItem()
	{
		TileEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

		if (handler != null)
		{
			for (ModuleItemStorage module : getStorageModules())
			{
				IItemHandler handler1 = module.getItemHandler();

				if (handler1 != null)
				{
					for (int i = 0; i < handler1.getSlots(); i++)
					{
						ItemStack stack = handler1.extractItem(i, 1, true);

						if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack))
						{
							ItemStack stack1 = ItemHandlerHelper.insertItem(handler, stack, false);

							if (stack.getCount() != stack1.getCount())
							{
								handler1.extractItem(i, stack.getCount() - stack1.getCount(), false);
								return true;
							}
						}
					}
				}
			}
		}

		return false;
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
			pipe.markDirty();

			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new TextComponentString("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}