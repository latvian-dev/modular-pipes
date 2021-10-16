package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.TransportPipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * @author LatvianModder
 */
public enum PipeTier {
	BASIC("basic", TransportPipeBlockEntity::new, 0),
	MK1("mk1", ModularPipeBlockEntity::new, 1),
	MK2("mk2", ModularPipeBlockEntity::new, 3),
	MK3("mk3", ModularPipeBlockEntity::new, 6);

	public final String name;
	public final BlockEntityType.BlockEntitySupplier<? extends PipeBlockEntity> blockEntity;
	public final int maxModules;

	PipeTier(String n, BlockEntityType.BlockEntitySupplier<? extends PipeBlockEntity> be, int m) {
		name = n;
		blockEntity = be;
		maxModules = m;
	}
}