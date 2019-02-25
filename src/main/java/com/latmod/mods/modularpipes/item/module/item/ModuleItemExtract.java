package com.latmod.mods.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
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
public class ModuleItemExtract extends ModuleItemHandler
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
		tick++;

		if (tick >= 7)
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
				extractItem();
			}

			tick = 0;
		}
	}

	private void extractItem()
	{
		TileEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());

		if (handler != null)
		{
			for (int slot = 0; slot < handler.getSlots(); slot++)
			{
				ItemStack stack = handler.extractItem(slot, 1, true);

				if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack))
				{
					ItemStack stack1 = insertItem(0, stack, false);

					if (stack1.getCount() != stack.getCount())
					{
						handler.extractItem(slot, stack.getCount() - stack1.getCount(), false);
						break;
					}
				}
			}
		}
	}
}