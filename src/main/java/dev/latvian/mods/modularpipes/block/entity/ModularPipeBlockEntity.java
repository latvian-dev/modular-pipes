package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.util.ServerPipeNetwork;

/**
 * @author LatvianModder
 */
public class ModularPipeBlockEntity extends PipeBlockEntity {
	public ModularPipeBlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE.get());
	}

	public void tickPipe(ServerPipeNetwork network) {
		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.updateModule(network);
			}
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();

		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.clearCache();
			}
		}
	}
}