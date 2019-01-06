package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.IPipeItemHandler;
import com.latmod.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipeBase extends Block
{
	public static final float SIZE = 4F;
	public static final PropertyBool[] CONNECTION = new PropertyBool[6];
	public static final AxisAlignedBB[] BOXES_64 = new AxisAlignedBB[1 << 6];

	static
	{
		for (int i = 0; i < 6; i++)
		{
			CONNECTION[i] = PropertyBool.create(EnumFacing.VALUES[i].getName());
		}

		double d0 = SIZE / 16D;
		double d1 = 1D - d0;

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

	public BlockPipeBase(MapColor color)
	{
		super(Material.ROCK, color);
		setHardness(0.35F);
		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState()
				.withProperty(CONNECTION[0], false)
				.withProperty(CONNECTION[1], false)
				.withProperty(CONNECTION[2], false)
				.withProperty(CONNECTION[3], false)
				.withProperty(CONNECTION[4], false)
				.withProperty(CONNECTION[5], false)
		);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, CONNECTION);
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
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

	public boolean isConnected(TilePipeBase pipe, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
		return tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase)
		{
			TilePipeBase pipe = (TilePipeBase) tileEntity;

			int id = 0;

			for (int i = 0; i < 6; i++)
			{
				if (isConnected(pipe, world, pos, EnumFacing.VALUES[i]))
				{
					id |= 1 << i;
				}
			}

			return BOXES_64[id];
		}

		return BOXES_64[0];
	}

	@Override
	@Deprecated
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
	{
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES_64[0]);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase)
		{
			TilePipeBase pipe = (TilePipeBase) tileEntity;

			for (int i = 0; i < 6; i++)
			{
				if (isConnected(pipe, world, pos, EnumFacing.VALUES[i]))
				{
					addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES_64[1 << i]);
				}
			}
		}
	}

	@Override
	@Deprecated
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity tileEntity = world.getTileEntity(pos.offset(side));

		if (tileEntity != null && tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()) instanceof IPipeItemHandler)
		{
			return false;
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase)
		{
			TilePipeBase pipe = (TilePipeBase) tileEntity;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				if (isConnected(pipe, world, pos, facing))
				{
					state = state.withProperty(CONNECTION[facing.getIndex()], true);
				}
			}
		}

		return state;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TilePipeBase)
			{
				((TilePipeBase) tileEntity).dropItems();
			}
		}

		super.breakBlock(world, pos, state);
	}
}