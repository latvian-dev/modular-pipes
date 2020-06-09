package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.client.ModularPipesClientConfig;
import com.latmod.mods.modularpipes.item.ItemBlockTank;
import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.TileTank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockTank extends Block
{
	public BlockTank()
	{
		super(Block.Properties.create(Material.GLASS, MaterialColor.BLACK).hardnessAndResistance(0.4f).sound(SoundType.GLASS));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new TileTank();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items)
	{
		items.add(new ItemStack(ModularPipesItems.TANK));

		if (ModularPipesClientConfig.general.add_all_tanks)
		{
			//			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
			//			{
			//				ItemBlockTank.TankCapProvider data = ItemBlockTank.getData(new ItemStack(ModularPipesItems.TANK));
			//				data.tank.setFluid(new FluidStack(fluid, 16 * Fluid.BUCKET_VOLUME));
			//				items.add(data.stack);
			//			}
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
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

		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);

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
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param)
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
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
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (player.abilities.isCreativeMode)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTank)
			{
				((TileTank) tileEntity).brokenByCreative = true;
			}
		}
	}

	//	@Override
	//	@SuppressWarnings("deprecation")
	//	public void breakBlock(World world, BlockPos pos, BlockState state)
	//	{
	//		TileEntity tileEntity = world.getTileEntity(pos);
	//
	//		if (tileEntity instanceof TileTank)
	//		{
	//			TileTank tile = (TileTank) tileEntity;
	//
	//			if (!tile.brokenByCreative)
	//			{
	//				ItemStack stack = super.getItem(world, pos, state);
	//
	//				if (tile.tank.getFluidAmount() > 0)
	//				{
	//					ItemBlockTank.TankCapProvider data = ItemBlockTank.getData(stack);
	//
	//					if (data != null)
	//					{
	//						data.tank.setFluid(tile.tank.getFluid().copy());
	//					}
	//				}
	//
	//				spawnAsEntity(world, pos, stack);
	//			}
	//		}
	//
	//		super.breakBlock(world, pos, state);
	//	}
}