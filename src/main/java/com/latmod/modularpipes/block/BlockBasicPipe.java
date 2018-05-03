package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TileBasicPipe;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockBasicPipe extends BlockPipeBase
{
	public BlockBasicPipe(String id, boolean o)
	{
		super(id, MapColor.GRAY, o);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileBasicPipe();
	}
}