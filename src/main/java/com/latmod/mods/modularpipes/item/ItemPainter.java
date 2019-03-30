package com.latmod.mods.modularpipes.item;

import com.latmod.mods.modularpipes.block.PipeSkin;
import com.latmod.mods.modularpipes.gui.ModularPipesGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemPainter extends Item
{
	@SuppressWarnings("deprecation")
	public static IBlockState getBlockState(@Nullable ItemStack item)
	{
		if (item == null)
		{
			return Blocks.AIR.getDefaultState();
		}

		Block block = Block.getBlockFromItem(item.getItem());

		if (block == Blocks.AIR)
		{
			return Blocks.AIR.getDefaultState();
		}

		try
		{
			return block.getStateFromMeta(item.getItem().getMetadata(item));
		}
		catch (Exception e)
		{
			return Blocks.AIR.getDefaultState();
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean isValidPaint(IBlockState s)
	{
		if (s.getBlock() instanceof BlockAir)
		{
			return false;
		}

		Block b = s.getBlock();
		return b instanceof BlockGlass || b instanceof BlockStainedGlass || (!(s instanceof IExtendedBlockState) && !b.getTickRandomly() && b.isTopSolid(s) && s.getRenderType() == EnumBlockRenderType.MODEL);
	}

	public static boolean isValidPaint(ItemStack stack)
	{
		try
		{
			return isValidPaint(getBlockState(stack));
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public static ItemStack getPaint(ItemStack stack)
	{
		if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("paint"))
		{
			return ItemStack.EMPTY;
		}

		ItemStack stack1 = new ItemStack(stack.getTagCompound().getCompoundTag("paint"));
		return stack1.isEmpty() ? ItemStack.EMPTY : stack1;
	}

	public static boolean setPaint(ItemStack stack, ItemStack paint)
	{
		if (stack.isEmpty() || !isValidPaint(paint))
		{
			return false;
		}

		stack.setTagInfo("paint", ItemHandlerHelper.copyStackWithSize(paint, 1).serializeNBT());
		return true;
	}

	public ItemPainter()
	{
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (hand == EnumHand.OFF_HAND)
		{
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		if (!world.isRemote)
		{
			ModularPipesGuiHandler.open(ModularPipesGuiHandler.PAINTER, player, 0, 0, 0);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		ItemStack paint = getPaint(stack);

		if (paint.isEmpty())
		{
			tooltip.add(I18n.format(PipeSkin.NONE.translationKey));
		}
		else
		{
			tooltip.add(paint.getDisplayName());
		}
	}
}