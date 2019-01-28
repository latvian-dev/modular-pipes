package com.latmod.modularpipes.item.module.item;

import com.latmod.modularpipes.item.module.SidePipeModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * @author LatvianModder
 */
public class ModuleItemStorage extends SidePipeModule
{
	public static final Comparator<ModuleItemStorage> COMPARATOR = (o1, o2) ->
	{
		int i = Integer.compare(o2.priority, o1.priority);

		if (i == 0)
		{
			return Boolean.compare(o1.filter.isEmpty(), o2.filter.isEmpty());
		}

		return i;
	};

	public ItemStack filter = ItemStack.EMPTY;
	public int priority = 0;
	private TileEntity tileEntity = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}

		if (priority != 0)
		{
			nbt.setInteger("priority", priority);
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

		priority = nbt.getInteger("priority");
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

	@Override
	public void clearCache()
	{
		tileEntity = null;
	}

	@Nullable
	public IItemHandler getItemHandler()
	{
		if (tileEntity == null || tileEntity.isInvalid())
		{
			tileEntity = getFacingTile();
		}

		return tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
	}
}