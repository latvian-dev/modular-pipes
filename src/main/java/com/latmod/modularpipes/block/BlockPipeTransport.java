package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeBase;
import com.latmod.modularpipes.tile.TilePipeTransport;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockPipeTransport extends BlockPipeBase
{
	public BlockPipeTransport()
	{
		super(MapColor.GRAY);
	}

	@Override
	public TilePipeBase createTileEntity(World world, IBlockState state)
	{
		return new TilePipeTransport();
	}
}