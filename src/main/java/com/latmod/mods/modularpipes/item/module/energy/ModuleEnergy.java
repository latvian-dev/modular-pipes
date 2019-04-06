package com.latmod.mods.modularpipes.item.module.energy;

import com.latmod.mods.modularpipes.ModularPipesConfig;
import com.latmod.mods.modularpipes.item.module.SidedPipeModule;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModuleEnergy extends SidedPipeModule implements IEnergyStorage
{
	private Optional<IEnergyStorage> cachedEnergyStorage = null;

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY && facing == side || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY && facing == side ? (T) this : super.getCapability(capability, facing);
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		cachedEnergyStorage = null;
	}

	@Nullable
	public IEnergyStorage getFacingEnergyStorage()
	{
		if (cachedEnergyStorage == null)
		{
			TileEntity tileEntity = getFacingTile();
			cachedEnergyStorage = Optional.ofNullable(tileEntity == null ? null : tileEntity.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()));
		}

		return cachedEnergyStorage.orElse(null);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}

	@Override
	public int getEnergyStored()
	{
		return pipe == null ? 0 : pipe.storedPower;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return pipe == null ? 0 : ModularPipesConfig.pipes.max_energy_stored;
	}

	@Override
	public boolean canExtract()
	{
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return false;
	}
}