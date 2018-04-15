package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipesItems;
import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.data.NodeType;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class BlockPipeBasicNode extends BlockPipeBase
{
	public final boolean opaque;

	public BlockPipeBasicNode(String id, boolean o)
	{
		super(id, MapColor.GRAY);
		opaque = o;
		setDefaultState(blockState.getBaseState()
				.withProperty(BlockModularPipe.CON_D, 0)
				.withProperty(BlockModularPipe.CON_U, 0)
				.withProperty(BlockModularPipe.CON_N, 0)
				.withProperty(BlockModularPipe.CON_S, 0)
				.withProperty(BlockModularPipe.CON_W, 0)
				.withProperty(BlockModularPipe.CON_E, 0));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, BlockModularPipe.CON_D, BlockModularPipe.CON_U, BlockModularPipe.CON_N, BlockModularPipe.CON_S, BlockModularPipe.CON_W, BlockModularPipe.CON_E);
	}

	@Deprecated
	@Override
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(opaque ? ModularPipesItems.PIPE_BASIC_OPAQUE : ModularPipesItems.PIPE_BASIC);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return opaque ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
	}

	@Override
	@Deprecated
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(getItemDropped(state, world.rand, 0), 1, damageDropped(state));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		for (int i = 0; i < 6; i++)
		{
			state = state.withProperty(BlockModularPipe.CONNECTIONS[i], canConnectTo(state, world, pos, EnumFacing.VALUES[i]) ? 1 : 0);
		}

		return state;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		int sum = 0;

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			if (canConnectTo(state, world, pos, facing))
			{
				sum++;
			}
		}

		if (sum == 0 || sum == 2)
		{
			world.setBlockState(pos, opaque ? ModularPipesItems.PIPE_BASIC_OPAQUE.getDefaultState() : ModularPipesItems.PIPE_BASIC.getDefaultState());
		}
	}

	@Override
	public boolean canConnectTo(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		BlockPos pos1 = pos.offset(facing);
		IBlockState state1 = world.getBlockState(pos1);
		Block block1 = state1.getBlock();
		return block1 == ModularPipesItems.CONTROLLER || block1 instanceof IPipeBlock && ((IPipeBlock) block1).canPipeConnect(world, pos1, state1, facing.getOpposite());
	}

	@Override
	public int getConnectionIdFromState(@Nullable IBlockState state)
	{
		if (state == null)
		{
			return 0;
		}

		int c = 0;

		for (int facing = 0; facing < 6; facing++)
		{
			c |= Math.min(1, state.getValue(BlockModularPipe.CONNECTIONS[facing])) << facing;
		}

		return c;
	}

	@Override
	public NodeType getNodeType(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		return NodeType.SIMPLE;
	}
}