package dev.latvian.mods.modularpipes.item.module.energy;

import dev.latvian.mods.modularpipes.ModularPipesCommon;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author LatvianModder
 */
public class ModuleEnergyOutput extends ModuleEnergy {
	private boolean hasMovedEnergy = false;
	private int tick = 0;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateModule() {
		if (pipe.storedEnergy > 0) {
			IEnergyStorage storage = getFacingEnergyStorage();

			if (storage != null) {
				int a = storage.receiveEnergy(Math.min(240, pipe.storedEnergy), false);

				if (a > 0) {
					pipe.storedEnergy -= a;
					pipe.setChanged();
					hasMovedEnergy = true;
				}
			}
		}

		if (tick <= 0) {
			tick = pipe.getLevel().random.nextInt(40) + 8;

			if (hasMovedEnergy) {
				spawnParticle(ModularPipesCommon.SPARK);
				hasMovedEnergy = false;
			}
		} else {
			tick--;
		}
	}
}