package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.block.EnumMK;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * @author LatvianModder
 */
public class ModularPipeMK2BlockEntity extends ModularPipeMK1BlockEntity {
	public ModularPipeMK2BlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE_MK2);
	}

	public ModularPipeMK2BlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK() {
		return EnumMK.MK2;
	}
}