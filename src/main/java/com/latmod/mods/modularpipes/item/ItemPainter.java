package com.latmod.mods.modularpipes.item;

import com.latmod.mods.itemfilters.api.IPaintable;
import com.latmod.mods.itemfilters.api.PaintAPI;
import com.latmod.mods.modularpipes.gui.ModularPipesGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		IPaintable paintable = PaintAPI.get(world.getTileEntity(pos));

		if (paintable != null)
		{
			paintable.paint(Block.getStateById(getPaint(player.getHeldItem(hand))), facing, player.isSneaking());
			return EnumActionResult.SUCCESS;
		}
		else if (player.isSneaking())
		{
			setPaint(player.getHeldItem(hand), Block.getStateId(world.getBlockState(pos)));
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
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