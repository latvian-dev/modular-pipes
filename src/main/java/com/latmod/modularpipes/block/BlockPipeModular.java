package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeModular;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockPipeModular extends BlockPipeBase
{
	public BlockPipeModular()
	{
		super(MapColor.LIGHT_BLUE);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePipeModular();
	}
}