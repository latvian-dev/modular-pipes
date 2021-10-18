package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;

/**
 * @author LatvianModder
 */
public class ModuleEnergyInput extends ModuleEnergy {
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int a = Math.min(Math.min(maxReceive, 80), ModularPipesConfig.pipes.max_energy_stored - ((ModularPipeBlockEntity) sideData.entity).storedEnergy);

		if (a > 0 && !simulate) {
			((ModularPipeBlockEntity) sideData.entity).storedEnergy += a;
			sideData.entity.setChanged();
		}

		return a;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}