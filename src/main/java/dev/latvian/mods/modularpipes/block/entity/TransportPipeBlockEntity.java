package dev.latvian.mods.modularpipes.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
public class TransportPipeBlockEntity extends PipeBlockEntity {
	public TransportPipeBlockEntity(BlockPos pos, BlockState state) {
		super(ModularPipesBlockEntities.TRANSPORT_PIPE.get(), pos, state);
	}
}