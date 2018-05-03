package com.latmod.modularpipes.block;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.latmod.modularpipes.data.IPipe;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class BlockPipeBase extends BlockMPBase
{
	public static final float SIZE = 4F;
	public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[13];
	public static final AxisAlignedBB BOXES_64[] = new AxisAlignedBB[64];

	static
	{
		double d0 = SIZE / 16D;
		double d1 = 1D - d0;

		BOXES[0] = new AxisAlignedBB(d0, d0, d0, d1, d1, d1);
		BOXES[1] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES[2] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES[3] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES[4] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES[5] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES[6] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
		d0 = (SIZE - 1F) / 16D;
		d1 = 1D - d0;
		BOXES[7] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES[8] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES[9] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES[10] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES[11] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES[12] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);

		for (int i = 0; i < BOXES_64.length; i++)
		{
			boolean x0 = (i & (1 << EnumFacing.WEST.getIndex())) != 0;
			boolean x1 = (i & (1 << EnumFacing.EAST.getIndex())) != 0;
			boolean y0 = (i & (1 << EnumFacing.DOWN.getIndex())) != 0;
			boolean y1 = (i & (1 << EnumFacing.UP.getIndex())) != 0;
			boolean z0 = (i & (1 << EnumFacing.NORTH.getIndex())) != 0;
			boolean z1 = (i & (1 << EnumFacing.SOUTH.getIndex())) != 0;
			BOXES_64[i] = new AxisAlignedBB(x0 ? 0D : d0, y0 ? 0D : d0, z0 ? 0D : d0, x1 ? 1D : d1, y1 ? 1D : d1, z1 ? 1D : d1);
		}
	}

	public static final IUnlistedProperty<IPipe> PIPE = new IUnlistedProperty<IPipe>()
	{
		@Override
		public String getName()
		{
			return "pipe";
		}

		@Override
		public boolean isValid(IPipe value)
		{
			return true;
		}

		@Override
		public Class<IPipe> getType()
		{
			return IPipe.class;
		}

		@Override
		public String valueToString(IPipe value)
		{
			return String.valueOf(value);
		}
	};

	public final boolean opaque;

	public BlockPipeBase(String id, MapColor color, boolean o)
	{
		super(id, Material.ROCK, color);
		setHardness(1F);
		opaque = o;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {PIPE});
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	@Deprecated
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if (state instanceof IExtendedBlockState)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof IPipe)
			{
				return ((IExtendedBlockState) state).withProperty(PIPE, (IPipe) tileEntity);
			}
		}

		return state;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase)
		{
			((TilePipeBase) tileEntity).onNeighborChange();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.SOLID || !opaque && layer == BlockRenderLayer.CUTOUT;
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
	@Deprecated
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof IPipe)
		{
			return BOXES_64[((IPipe) tileEntity).getConnections()];
		}

		return BOXES_64[0];
	}

	@Override
	@Deprecated
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
	{
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[0]);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof IPipe)
		{
			IPipe pipe = (IPipe) tileEntity;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				if (pipe.getPipeConnectionType(facing).hasPipe())
				{
					addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[1 + facing.ordinal()]);
				}
			}
		}
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		RayTraceResult ray = ClientUtils.MC.objectMouseOver;

		if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
		{
			return BOXES[ray.subHit].offset(pos);
		}

		return super.getSelectedBoundingBox(state, world, pos);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof IPipe)
			{
				tileEntity.updateContainingBlockInfo();
				CommonUtils.notifyBlockUpdate(world, pos, state);
				PipeNetwork.get(world).addPipe(tileEntity);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof IPipe)
			{
				PipeNetwork.get(world).removePipe(tileEntity, false);
			}
		}

		super.breakBlock(world, pos, state);
	}
}