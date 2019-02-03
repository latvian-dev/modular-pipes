package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.item.ItemBlockTank;
import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.TileTank;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class BlockTank extends Block
{
	public static final PropertyBool TANK_DOWN = PropertyBool.create("down");
	public static final PropertyBool TANK_UP = PropertyBool.create("up");

	public BlockTank()
	{
		super(Material.GLASS, MapColor.BLACK);
		setHardness(0.4F);
		setSoundType(SoundType.GLASS);
		setDefaultState(blockState.getBaseState().withProperty(TANK_DOWN, false).withProperty(TANK_UP, false));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TANK_DOWN, TANK_UP);
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
		return new TileTank();
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
		return false;
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.getItem() == ModularPipesItems.TANK)
		{
			return false;
		}
		else if (world.isRemote)
		{
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TileTank))
		{
			return true;
		}

		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if (inv == null)
		{
			return true;
		}

		ItemStack stack1 = stack.copy();
		FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack1, ((TileTank) tileEntity).tank, inv, Integer.MAX_VALUE, player, true);

		if (!result.isSuccess())
		{
			result = FluidUtil.tryEmptyContainerAndStow(stack1, ((TileTank) tileEntity).tank, inv, Integer.MAX_VALUE, player, true);
		}

		if (result.isSuccess())
		{
			player.setHeldItem(hand, result.getResult());
		}

		return true;
	}

	@Override
	@Deprecated
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileTank)
		{
			TileTank tileTank = (TileTank) tileEntity;

			if (id == 0)
			{
				if (world.isRemote)
				{
					if (param == 0)
					{
						tileTank.tank.setFluid(null);
					}
					else if (tileTank.tank.getFluid() != null)
					{
						tileTank.tank.getFluid().amount = param * tileTank.tank.getCapacity() / 255;
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if (world.getBlockState(pos.down()).getBlock() == this)
		{
			state = state.withProperty(TANK_DOWN, true);
		}

		if (world.getBlockState(pos.up()).getBlock() == this)
		{
			state = state.withProperty(TANK_UP, true);
		}

		return state;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		ItemBlockTank.TankCapProvider data = ItemBlockTank.getData(stack);

		if (data != null && data.tank.getFluidAmount() > 0)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTank)
			{
				((TileTank) tileEntity).tank.setFluid(data.tank.getFluid().copy());
			}
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
	{
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTank)
			{
				((TileTank) tileEntity).brokenByCreative = true;
			}
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileTank)
		{
			TileTank tile = (TileTank) tileEntity;

			if (!tile.brokenByCreative)
			{
				ItemStack stack = super.getItem(world, pos, state);

				if (tile.tank.getFluidAmount() > 0)
				{
					ItemBlockTank.TankCapProvider data = ItemBlockTank.getData(stack);

					if (data != null)
					{
						data.tank.setFluid(tile.tank.getFluid().copy());
					}
				}

				spawnAsEntity(world, pos, stack);
			}
		}

		super.breakBlock(world, pos, state);
	}
}