package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.block.EnumMK;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * @author LatvianModder
 */
public class ModularPipeMK3BlockEntity extends ModularPipeMK2BlockEntity {
	public ModularPipeMK3BlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE_MK3);
	}

	public ModularPipeMK3BlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK() {
		return EnumMK.MK3;
	}
}