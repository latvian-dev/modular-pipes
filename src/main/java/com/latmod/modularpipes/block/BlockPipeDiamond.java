package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.gui.ModularPipesGuiHandler;
import com.latmod.modularpipes.tile.EnumDiamondPipeMode;
import com.latmod.modularpipes.tile.TileDiamondPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * @author LatvianModder
 */
public class BlockPipeDiamond extends Block
{
	public static final PropertyEnum<EnumDiamondPipeMode>[] CONNECTION = new PropertyEnum[6];

	static
	{
		for (int i = 0; i < 6; i++)
		{
			CONNECTION[i] = PropertyEnum.create(EnumFacing.VALUES[i].getName(), EnumDiamondPipeMode.class);
		}
	}

	public BlockPipeDiamond()
	{
		super(Material.IRON, MapColor.STONE);
		setHardness(2F);
		setDefaultState(blockState.getBaseState()
				.withProperty(CONNECTION[0], EnumDiamondPipeMode.DISABLED)
				.withProperty(CONNECTION[1], EnumDiamondPipeMode.DISABLED)
				.withProperty(CONNECTION[2], EnumDiamondPipeMode.DISABLED)
				.withProperty(CONNECTION[3], EnumDiamondPipeMode.DISABLED)
				.withProperty(CONNECTION[4], EnumDiamondPipeMode.DISABLED)
				.withProperty(CONNECTION[5], EnumDiamondPipeMode.DISABLED)
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
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileDiamondPipe();
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
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		BlockPos pos1 = pos.offset(side);
		IBlockState state1 = world.getBlockState(pos1);

		if (state1.getBlock() instanceof IPipeBlock && ((IPipeBlock) state1.getBlock()).hasPipeConnection(state1, world, pos1, side.getOpposite()))
		{
			return false;
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(ModularPipes.INSTANCE, ModularPipesGuiHandler.DIAMOND_PIPE, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileDiamondPipe)
		{
			TileDiamondPipe pipe = (TileDiamondPipe) tileEntity;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				EnumDiamondPipeMode mode = pipe.inventories[facing.getIndex()].mode;

				if (mode != EnumDiamondPipeMode.DISABLED)
				{
					TileEntity tileEntity1 = world.getTileEntity(pos.offset(facing));

					if (tileEntity1 != null && tileEntity1.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
					{
						state = state.withProperty(CONNECTION[facing.getIndex()], mode);
					}
				}
			}
		}

		return state;
	}
}