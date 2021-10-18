package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModuleEnergy extends PipeModule implements IEnergyStorage {
	private Optional<IEnergyStorage> cachedEnergyStorage = null;

	@Override
	public boolean isThisCapability(Capability<?> capability) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		cachedEnergyStorage = null;
	}

	@Nullable
	public IEnergyStorage getFacingEnergyStorage() {
		if (cachedEnergyStorage == null) {
			BlockEntity tileEntity = getFacingTile();
			cachedEnergyStorage = Optional.ofNullable(tileEntity == null ? null : tileEntity.getCapability(CapabilityEnergy.ENERGY, sideData.direction.getOpposite()).orElse(null));
		}

		return cachedEnergyStorage.orElse(null);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return ((ModularPipeBlockEntity) sideData.entity).storedEnergy;
	}

	@Override
	public int getMaxEnergyStored() {
		return ModularPipesConfig.pipes.max_energy_stored;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}