package dev.latvian.mods.modularpipes.item.module;

/**
 * @author LatvianModder
 */
public class EnergyInputModule extends ModuleEnergy {
	/*
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
	 */
}