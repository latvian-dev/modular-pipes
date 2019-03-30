package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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
public class BlockPipeBase extends Block
{
	public static final float SIZE = 4F;
	public static final AxisAlignedBB[] BOXES_64 = new AxisAlignedBB[1 << 6];
	public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[7];

	static
	{
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

		BOXES[0] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES[1] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES[2] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES[3] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES[4] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES[5] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
		BOXES[6] = new AxisAlignedBB(d0, d0, d0, d1, d1, d1);
	}

	public static final IUnlistedProperty<TilePipeBase> PIPE = new IUnlistedProperty<TilePipeBase>()
	{
		@Override
		public String getName()
		{
			return "pipe";
		}

		@Override
		public boolean isValid(TilePipeBase value)
		{
			return true;
		}

		@Override
		public Class<TilePipeBase> getType()
		{
			return TilePipeBase.class;
		}

		@Override
		public String valueToString(TilePipeBase value)
		{
			return TileEntity.getKey(value.getClass()).getPath();
		}
	};

	public BlockPipeBase(MapColor color)
	{
		super(Material.ROCK, color);
		setHardness(0.35F);
		setSoundType(SoundType.METAL);
	}

	public boolean isModular()
	{
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {PIPE});
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
	public TilePipeBase createTileEntity(World world, IBlockState state)
	{
		return new TilePipeBase();
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
	@Deprecated
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state)
	{
		return 1F;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
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
				if (pipe.isConnected(EnumFacing.VALUES[i]))
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
				if (pipe.isConnected(EnumFacing.VALUES[i]))
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
		return false;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if (state instanceof IExtendedBlockState)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TilePipeBase)
			{
				return ((IExtendedBlockState) state).withProperty(PIPE, (TilePipeBase) tileEntity);
			}
		}

		return state;
	}
}