package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.tile.TilePipeBase;
import com.latmod.mods.modularpipes.tile.TilePipeTransport;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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

	private void updatePipe(IBlockState state, World world, BlockPos pos, int loop)
	{
		if (loop > 1)
		{
			return;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeTransport)
		{
			TilePipeTransport pipe = (TilePipeTransport) tileEntity;
			pipe.end1 = null;
			pipe.end2 = null;

			int count = 0;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				TileEntity tileEntity1 = world.getTileEntity(pos.offset(facing));

				if (tileEntity1 instanceof TilePipeBase && pipe.canPipesConnect(((TilePipeBase) tileEntity1).paint))
				{
					if (pipe.end1 == null)
					{
						pipe.end1 = facing;
					}
					else if (pipe.end2 == null)
					{
						pipe.end2 = facing;
					}

					count++;
				}
			}

			if (count > 2)
			{
				pipe.end1 = null;
				pipe.end2 = null;
			}

			world.notifyBlockUpdate(pos, state, state, 8);

			if (count > 2)
			{
				for (EnumFacing facing : EnumFacing.VALUES)
				{
					BlockPos pos1 = pos.offset(facing);
					IBlockState state1 = world.getBlockState(pos1);

					if (state1.getBlock() instanceof BlockPipeTransport)
					{
						((BlockPipeTransport) state1.getBlock()).updatePipe(state1, world, pos1, loop + 1);
					}
				}
			}
			else
			{
				if (pipe.end1 != null)
				{
					BlockPos pos1 = pos.offset(pipe.end1);
					world.notifyBlockUpdate(pos1, world.getBlockState(pos1), world.getBlockState(pos1), 8);
				}

				if (pipe.end2 != null)
				{
					BlockPos pos2 = pos.offset(pipe.end2);
					world.notifyBlockUpdate(pos2, world.getBlockState(pos2), world.getBlockState(pos2), 8);
				}
			}
		}
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		updatePipe(state, world, pos, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		updatePipe(state, world, pos, 0);
	}
}