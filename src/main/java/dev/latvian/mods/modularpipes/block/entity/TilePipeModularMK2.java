package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.block.EnumMK;
import dev.latvian.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK2 extends TilePipeModularMK1 {
	public TilePipeModularMK2() {
		super(ModularPipesTiles.PIPE_MODULAR_MK2);
	}

	public TilePipeModularMK2(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK() {
		return EnumMK.MK2;
	}
}