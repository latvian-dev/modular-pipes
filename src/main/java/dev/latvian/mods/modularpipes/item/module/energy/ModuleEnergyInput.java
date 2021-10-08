package dev.latvian.mods.modularpipes.item.module.energy;

import dev.latvian.mods.modularpipes.ModularPipesConfig;

/**
 * @author LatvianModder
 */
public class ModuleEnergyInput extends ModuleEnergy {
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (pipe == null) {
			return 0;
		}

		int a = Math.min(Math.min(maxReceive, 80), ModularPipesConfig.pipes.max_energy_stored - pipe.storedPower);

		if (a > 0 && !simulate) {
			pipe.storedPower += a;
			pipe.markDirty();
			pipe.sync = false;
		}

		return a;
	}

	@Override
	public boolean canReceive() {
		return pipe != null;
	}
}