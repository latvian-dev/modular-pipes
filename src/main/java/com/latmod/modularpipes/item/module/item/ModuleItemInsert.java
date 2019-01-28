package com.latmod.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class ModuleItemInsert extends ModuleItemHandler
{
	public int tick = 0;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (tick > 0)
		{
			nbt.setByte("tick", (byte) tick);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		tick = nbt.getByte("tick");
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

		if (tick == 0)
		{
			World w = pipe.getWorld();

			if (w.isRemote)
			{
				BlockPos p = pipe.getPos();
				double x = p.getX() + 0.5D + side.getXOffset() * 0.35D;
				double y = p.getY() + 0.5D + side.getYOffset() * 0.35D;
				double z = p.getZ() + 0.5D + side.getZOffset() * 0.35D;
				w.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, 0D, 0D, 0D);
			}
			else
			{
				TileEntity tile = getFacingTile();
				IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

				if (handler != null)
				{
					for (ModuleItemStorage module : getStorageModules(ItemStack.EMPTY))
					{
						IItemHandler handler1 = module.getItemHandler();

						if (handler1 != null)
						{
							for (int i = 0; i < handler1.getSlots(); i++)
							{
								ItemStack stack = handler1.getStackInSlot(i);
								if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack))
								{
									stack = handler1.extractItem(i, 1, true);

									if (!stack.isEmpty())
									{
										ItemStack stack1 = ItemHandlerHelper.insertItem(handler, stack, false);

										if (stack1.getCount() != stack.getCount())
										{
											handler1.extractItem(i, stack.getCount() - stack1.getCount(), false);
											return;
										}
									}
								}
							}
						}
					}

					//FIXME
				}
			}
		}

		tick++;

		if (tick >= 20)
		{
			tick = 0;
		}
	}
}