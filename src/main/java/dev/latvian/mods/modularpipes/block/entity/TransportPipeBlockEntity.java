package dev.latvian.mods.modularpipes.block.entity;

/**
 * @author LatvianModder
 */
public class TransportPipeBlockEntity extends PipeBlockEntity {
	public TransportPipeBlockEntity() {
		super(ModularPipesBlockEntities.TRANSPORT_PIPE.get());
	}

	public boolean isValidSegment(int ignore) {
		int c = 0;

		for (int i = 0; i < 6; i++) {
			if (i != ignore && sideData[i].connect) {
				c++;
			}
		}

		return c == 1;
	}
}