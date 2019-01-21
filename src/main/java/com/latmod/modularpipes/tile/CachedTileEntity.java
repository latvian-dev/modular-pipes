package com.latmod.modularpipes.tile;

import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class CachedTileEntity
{
	public static final CachedTileEntity NONE = new CachedTileEntity(null, 0);

	public final TileEntity tile;
	public final int distance;

	public CachedTileEntity(@Nullable TileEntity t, int d)
	{
		tile = t;
		distance = d;
	}
}