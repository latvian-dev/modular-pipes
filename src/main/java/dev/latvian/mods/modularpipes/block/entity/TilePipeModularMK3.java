package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.block.EnumMK;
import dev.latvian.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK3 extends TilePipeModularMK2 {
	public TilePipeModularMK3() {
		super(ModularPipesTiles.PIPE_MODULAR_MK3);
	}

	public TilePipeModularMK3(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK() {
		return EnumMK.MK3;
	}
}