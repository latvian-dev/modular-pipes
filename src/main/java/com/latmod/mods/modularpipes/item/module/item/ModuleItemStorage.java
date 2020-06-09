package com.latmod.mods.modularpipes.item.module.item;

import com.latmod.mods.modularpipes.item.module.SidedPipeModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * @author LatvianModder
 */
public class ModuleItemStorage extends SidedPipeModule
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
	public void writeData(CompoundNBT nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.put("filter", filter.serializeNBT());
		}

		if (priority != 0)
		{
			nbt.putInt("priority", priority);
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

		priority = nbt.getInt("priority");
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
			pipe.markDirty();

			if (!player.world.isRemote)
			{
				player.sendStatusMessage(new StringTextComponent("Filter changed to " + filter.getDisplayName()), true); //LANG
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
		if (tileEntity == null || tileEntity.isRemoved())
		{
			tileEntity = getFacingTile();
		}

		return tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);
	}
}