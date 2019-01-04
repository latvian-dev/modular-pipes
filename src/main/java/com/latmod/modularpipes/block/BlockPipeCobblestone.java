package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.PipeItem;
import com.latmod.modularpipes.tile.TileCobblestonePipe;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockPipeCobblestone extends BlockRotatedPillar implements IPipeBlock
{
	public static final AxisAlignedBB[] PIPE_AABB = new AxisAlignedBB[3];

	static
	{
		double d0 = 4D / 16D;
		double d1 = 12D / 16D;
		PIPE_AABB[EnumFacing.Axis.X.ordinal()] = new AxisAlignedBB(0, d0, d0, 1, d1, d1);
		PIPE_AABB[EnumFacing.Axis.Y.ordinal()] = new AxisAlignedBB(d0, 0, d0, d1, 1, d1);
		PIPE_AABB[EnumFacing.Axis.Z.ordinal()] = new AxisAlignedBB(d0, d0, 0, d1, d1, 1);
	}

	public BlockPipeCobblestone()
	{
		super(Material.ROCK, MapColor.STONE);
		setHardness(0.35F);
		setSoundType(SoundType.STONE);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileCobblestonePipe();
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return PIPE_AABB[state.getValue(AXIS).ordinal()];
	}

	@Override
	@Deprecated
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		if (side.getAxis() == state.getValue(AXIS))
		{
			BlockPos pos1 = pos.offset(side);
			IBlockState state1 = world.getBlockState(pos1);

			if (state1.getBlock() instanceof IPipeBlock && ((IPipeBlock) state1.getBlock()).hasPipeConnection(state1, world, pos1, side.getOpposite()))
			{
				return false;
			}
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileCobblestonePipe)
		{
			TileCobblestonePipe pipe = (TileCobblestonePipe) tileEntity;

			for (PipeItem item : pipe.fromNegative.items)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item.stack);
			}

			for (PipeItem item : pipe.fromPositive.items)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item.stack);
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean hasPipeConnection(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		return state.getValue(AXIS) == facing.getAxis();
	}
}