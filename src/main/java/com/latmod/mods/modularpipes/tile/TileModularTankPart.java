package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TileModularTankPart extends TileBase
{
	public TileModularTankPart()
	{
		this(ModularPipesTiles.MODULAR_TANK_PART);
	}

	public TileModularTankPart(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}
}