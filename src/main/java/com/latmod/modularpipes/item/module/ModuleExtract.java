package com.latmod.modularpipes.item.module;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class ModuleExtract extends SidePipeModule
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
	public void updateModule()
	{
		if (tick == 0)
		{
			World w = pipe.getWorld();
			BlockPos p = pipe.getPos();

			if (w.isRemote)
			{
				w.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D, 0D, 0D, 0D);
			}
			else
			{
				extractItem();
			}
		}

		tick++;

		if (tick >= 20)
		{
			tick = 0;
		}
	}

	public void extractItem()
	{
		TileEntity tile = getFacingTile();

		if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()))
		{
			IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

			if (handler != null)
			{
				int slot = -1;

				for (int i = 0; i < handler.getSlots(); i++)
				{
					ItemStack stack = handler.extractItem(i, 1, true);

					if (!stack.isEmpty())
					{
						slot = i;
						break;
					}
				}

				if (slot != -1)
				{
					/* FIXME
					PipeItem item = new PipeItem(container.getNetwork());
					item.stack = stack;

					if (item.generatePath(container))
					{
						handler.extractItem(slot, 1, false);
						item.addToNetwork();
					}
					*/
				}
			}
		}
	}
}