package com.latmod.mods.modularpipes.item;

import com.latmod.mods.modularpipes.gui.ModularPipesGuiHandler;
import com.latmod.mods.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class ItemPainter extends Item
{
	public static int getPaint(ItemStack stack)
	{
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("paint") : 0;
	}

	public static void setPaint(ItemStack stack, int paint)
	{
		if (paint == 0)
		{
			if (stack.hasTagCompound())
			{
				stack.getTagCompound().removeTag("paint");

				if (stack.getTagCompound().isEmpty())
				{
					stack.setTagCompound(null);
				}
			}
		}
		else
		{
			stack.setTagInfo("paint", new NBTTagInt(paint));
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean setPaint(ItemStack stack, ItemStack paint)
	{
		if (paint.isEmpty())
		{
			setPaint(stack, 0);
			return true;
		}

		try
		{
			Block block = Block.getBlockFromItem(paint.getItem());

			if (block instanceof BlockAir)
			{
				return false;
			}

			setPaint(stack, Block.getStateId(block.getStateFromMeta(paint.getItem().getMetadata(paint))));
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public ItemPainter()
	{
		setMaxStackSize(1);
	}

	@Override
	@SuppressWarnings("deprecation")
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase)
		{
			if (player.isSneaking())
			{
				HashSet<TilePipeBase> pipes = new HashSet<>();
				paintAll((TilePipeBase) tileEntity, ((TilePipeBase) tileEntity).paint, pipes, getPaint(player.getHeldItem(hand)));

				for (TilePipeBase pipe : pipes)
				{
					pipe.markDirty();
					IBlockState state = world.getBlockState(pipe.getPos());
					state.getBlock().neighborChanged(state, world, pipe.getPos(), state.getBlock(), pipe.getPos().offset(facing));

					if (world.isRemote)
					{
						world.notifyBlockUpdate(pipe.getPos(), state, state, 11);
					}
				}
			}
			else
			{
				((TilePipeBase) tileEntity).paint = getPaint(player.getHeldItem(hand));
				tileEntity.markDirty();
				IBlockState state = world.getBlockState(pos);
				state.getBlock().neighborChanged(state, world, pos, state.getBlock(), pos.offset(facing));
				world.notifyNeighborsOfStateChange(pos, state.getBlock(), true);

				if (world.isRemote)
				{
					world.notifyBlockUpdate(pos, state, state, 11);
				}
			}

			return EnumActionResult.SUCCESS;
		}
		else if (player.isSneaking())
		{
			setPaint(player.getHeldItem(hand), Block.getStateId(world.getBlockState(pos)));
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	private void paintAll(TilePipeBase pipe, int originalPaint, HashSet<TilePipeBase> visited, int paint)
	{
		if (pipe.paint == originalPaint)
		{
			pipe.paint = paint;
			visited.add(pipe);

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(facing));

				if (tileEntity instanceof TilePipeBase && !visited.contains(tileEntity))
				{
					paintAll((TilePipeBase) tileEntity, originalPaint, visited, paint);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote)
		{
			ModularPipesGuiHandler.open(ModularPipesGuiHandler.PAINTER, player, 0, 0, 0);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}