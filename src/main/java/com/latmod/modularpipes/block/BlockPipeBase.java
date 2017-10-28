package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class BlockPipeBase extends BlockMPBase implements IPipeBlock
{
	public static final float SIZE = 4F;
	public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[7];
	public static final AxisAlignedBB BOXES_64[] = new AxisAlignedBB[64];

	static
	{
		double d0 = SIZE / 16D;
		double d1 = 1D - d0;

		BOXES[0] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES[1] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES[2] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES[3] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES[4] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES[5] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
		BOXES[6] = new AxisAlignedBB(d0, d0, d0, d1, d1, d1);

		for (int i = 0; i < BOXES_64.length; i++)
		{
			boolean x0 = (i & MathUtils.FACING_BIT_WEST) != 0;
			boolean x1 = (i & MathUtils.FACING_BIT_EAST) != 0;
			boolean y0 = (i & MathUtils.FACING_BIT_DOWN) != 0;
			boolean y1 = (i & MathUtils.FACING_BIT_UP) != 0;
			boolean z0 = (i & MathUtils.FACING_BIT_NORTH) != 0;
			boolean z1 = (i & MathUtils.FACING_BIT_SOUTH) != 0;
			BOXES_64[i] = new AxisAlignedBB(x0 ? 0D : d0, y0 ? 0D : d0, z0 ? 0D : d0, x1 ? 1D : d1, y1 ? 1D : d1, z1 ? 1D : d1);
		}
	}

	public BlockPipeBase(String id, MapColor color)
	{
		super(id, Material.ROCK, color);
		setHardness(1F);
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Deprecated
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	@Override
	@Deprecated
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[6]);

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			if (canConnectTo(state, worldIn, pos, facing))
			{
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[facing.ordinal()]);
			}
		}
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return BOXES_64[getConnectionIdFromState(getActualState(state, source, pos))];
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		RayTraceResult ray = ClientUtils.MC.objectMouseOver;

		if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
		{
			return BOXES[ray.subHit].offset(pos);
		}

		return super.getSelectedBoundingBox(state, worldIn, pos);
	}

	public boolean canConnectTo(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
	{
		BlockPos pos1 = pos.offset(facing);
		IBlockState state1 = worldIn.getBlockState(pos1);
		Block block1 = state1.getBlock();
		return block1 instanceof IPipeBlock && ((IPipeBlock) block1).canPipeConnect(worldIn, pos1, state1, facing.getOpposite());

	}

	public int getConnectionIdFromState(@Nullable IBlockState state)
	{
		return 0;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			PipeNetwork.get(worldIn).addPipe(pos, state);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			PipeNetwork.get(worldIn).removePipe(pos, false);
		}

		super.breakBlock(worldIn, pos, state);
	}
}