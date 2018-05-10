package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipesItems;
import com.latmod.modularpipes.tile.TileBasicPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
	public Block getOppositeOpaque()
	{
		return opaque ? ModularPipesItems.PIPE_BASIC : ModularPipesItems.PIPE_BASIC_OPAQUE;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileBasicPipe();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (player.isSneaking())
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileBasicPipe)
			{
				world.removeTileEntity(pos);
			}

			world.setBlockState(pos, getOppositeOpaque().getDefaultState());

			if (tileEntity instanceof TileBasicPipe)
			{
				world.removeTileEntity(pos);
				tileEntity.validate();
				world.setTileEntity(pos, tileEntity);
			}

			return true;
		}

		return false;
	}
}