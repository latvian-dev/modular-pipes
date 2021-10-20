package dev.latvian.mods.modularpipes.item.module;

/**
 * @author LatvianModder
 */
public class ModuleEnergyOutput extends ModuleEnergy {
	/*
	private boolean hasMovedEnergy = false;
	private int tick = 0;

	@Override
	public void updateModule() {
		if (((ModularPipeBlockEntity) sideData.entity).storedEnergy > 0) {
			IEnergyStorage storage = getFacingEnergyStorage();

			if (storage != null) {
				int a = storage.receiveEnergy(Math.min(240, ((ModularPipeBlockEntity) sideData.entity).storedEnergy), false);

				if (a > 0) {
					((ModularPipeBlockEntity) sideData.entity).storedEnergy -= a;
					sideData.entity.setChanged();
					hasMovedEnergy = true;
				}
			}
		}

		if (tick <= 0) {
			tick = sideData.entity.getLevel().random.nextInt(40) + 8;

			if (hasMovedEnergy) {
				spawnParticle(ModularPipesCommon.SPARK);
				hasMovedEnergy = false;
			}
		} else {
			tick--;
		}
	}
	 */
}