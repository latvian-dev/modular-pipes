package com.latmod.mods.modularpipes.item;

import com.latmod.mods.itemfilters.api.IPaintable;
import com.latmod.mods.itemfilters.api.PaintAPI;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ItemPainter extends Item
{
	public ItemPainter(Properties properties)
	{
		super(properties);
	}

	public static int getPaint(ItemStack stack)
	{
		return stack.hasTag() ? stack.getTag().getInt("paint") : 0;
	}

	public static void setPaint(ItemStack stack, int paint)
	{
		if (paint == 0)
		{
			if (stack.hasTag())
			{
				stack.getTag().remove("paint");

				if (stack.getTag().isEmpty())
				{
					stack.setTag(null);
				}
			}
		}
		else
		{
			stack.setTagInfo("paint", new IntNBT(paint));
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

			if (block == Blocks.AIR)
			{
				return false;
			}

			setPaint(stack, Block.getStateId(block.getDefaultState()));
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		Direction facing = context.getFace();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		IPaintable paintable = PaintAPI.get(world.getTileEntity(pos));

		if (paintable != null)
		{
			paintable.paint(Block.getStateById(getPaint(player.getHeldItem(hand))), facing, player.isSneaking());
			return ActionResultType.SUCCESS;
		}
		else if (player.isSneaking())
		{
			setPaint(player.getHeldItem(hand), Block.getStateId(world.getBlockState(pos)));
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote)
		{
			//			ModularPipesGuiHandler.open(ModularPipesGuiHandler.PAINTER, player, 0, 0, 0);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
}