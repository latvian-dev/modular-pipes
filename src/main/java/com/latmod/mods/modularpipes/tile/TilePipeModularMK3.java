package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK3 extends TilePipeModularMK2
{
	public TilePipeModularMK3()
	{
		super(ModularPipesTiles.PIPE_MODULAR_MK3);
	}

	public TilePipeModularMK3(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Override
	public EnumMK getMK()
	{
		return EnumMK.MK3;
	}
}