package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.TransportPipeBlockEntity;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum PipeTier {
	BASIC("basic", TransportPipeBlockEntity::new, 0),
	MK1("mk1", ModularPipeBlockEntity::new, 1),
	MK2("mk2", ModularPipeBlockEntity::new, 3),
	MK3("mk3", ModularPipeBlockEntity::new, 6);

	public final String name;
	public final Supplier<? extends BasePipeBlockEntity> blockEntity;
	public final int maxModules;

	PipeTier(String n, Supplier<? extends BasePipeBlockEntity> te, int m) {
		name = n;
		blockEntity = te;
		maxModules = m;
	}
}