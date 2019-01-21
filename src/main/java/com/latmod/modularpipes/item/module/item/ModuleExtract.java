package com.latmod.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.item.module.PipeModule;
import com.latmod.modularpipes.item.module.SidePipeModule;
import com.latmod.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class ModuleExtract extends SidePipeModule
{
	public int tick = 0;
	public ItemStack filter = ItemStack.EMPTY;

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

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateModule()
	{
		if (tick == 0)
		{
			extractItem();
		}

		tick++;

		if (tick >= 20)
		{
			tick = 0;
		}
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

	public void extractItem()
	{
		World w = pipe.getWorld();

		if (w.isRemote)
		{
			BlockPos p = pipe.getPos();
			double x = p.getX() + 0.5D + side.getXOffset() * 0.35D;
			double y = p.getY() + 0.5D + side.getYOffset() * 0.35D;
			double z = p.getZ() + 0.5D + side.getZOffset() * 0.35D;
			w.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, 0D, 0D, 0D);
			return;
		}

		TileEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

		if (handler != null)
		{
			for (int slot = 0; slot < handler.getSlots(); slot++)
			{
				if (!handler.getStackInSlot(slot).isEmpty() && ItemFiltersAPI.filter(filter, handler.getStackInSlot(slot)))
				{
					ItemStack stack = handler.extractItem(slot, 1, true);

					if (!stack.isEmpty())
					{
						HashSet<TilePipeModularMK1> set = new HashSet<>();
						pipe.getNetwork(set);
						ArrayList<ModuleItemStorage> handlers = new ArrayList<>(1);

						for (TilePipeModularMK1 pipe1 : set)
						{
							for (PipeModule module : pipe1.modules)
							{
								if (module instanceof ModuleItemStorage && ItemFiltersAPI.filter(((ModuleItemStorage) module).filter, stack))
								{
									handlers.add((ModuleItemStorage) module);
								}
							}
						}

						if (!handlers.isEmpty())
						{
							System.out.println(handlers);
							return;
						}
					}
				}
			}
		}
	}
}