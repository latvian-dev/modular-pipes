package com.latmod.mods.modularpipes.item.module.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author LatvianModder
 */
public class ModuleEnergyOutput extends ModuleEnergy
{
	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateModule()
	{
		if (pipe.getWorld().isRemote || pipe.storedPower <= 0)
		{
			return;
		}

		TileEntity tileEntity = getFacingTile();
		IEnergyStorage storage = tileEntity != null ? tileEntity.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()) : null;

		if (storage != null)
		{
			int a = storage.receiveEnergy(Math.min(240, pipe.storedPower), false);

			if (a > 0)
			{
				pipe.storedPower -= a;
				pipe.markDirty();
				pipe.sync = false;
			}
		}
	}
}