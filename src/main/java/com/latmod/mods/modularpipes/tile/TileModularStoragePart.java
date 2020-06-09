package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author LatvianModder
 */
public class TileModularStoragePart extends TileBase
{
	public TileModularStoragePart()
	{
		this(ModularPipesTiles.MODULAR_STORAGE_PART);
	}

	public TileModularStoragePart(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}
}