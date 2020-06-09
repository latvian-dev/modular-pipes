package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK2 extends TilePipeModularMK1
{
	public TilePipeModularMK2()
	{
		super(ModularPipesTiles.PIPE_MODULAR_MK2);
	}

	public TilePipeModularMK2(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK()
	{
		return EnumMK.MK2;
	}
}