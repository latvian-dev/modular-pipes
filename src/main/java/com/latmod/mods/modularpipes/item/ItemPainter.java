package com.latmod.mods.modularpipes.item;

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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemPainter extends Item
{
	@SuppressWarnings("deprecation")
	public static IBlockState getBlockState(ItemStack item)
	{
		Block block = Block.getBlockFromItem(item.getItem());

		if (block instanceof BlockAir)
		{
			return Blocks.AIR.getDefaultState();
		}

		return block.getStateFromMeta(item.getItem().getMetadata(item));
	}

	public static ItemStack getItemStack(IBlockState state)
	{
		if (state.getBlock() instanceof BlockAir)
		{
			return ItemStack.EMPTY;
		}

		Item item = Item.getItemFromBlock(state.getBlock());

		if (item != Items.AIR)
		{
			return new ItemStack(item, 1, state.getBlock().damageDropped(state));
		}

		return ItemStack.EMPTY;
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

	@Nullable
	public static ItemStack getValidPaint(ItemStack paint)
	{
		if (paint.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		if (!(paint.getItem() instanceof ItemBlock) || paint.isItemStackDamageable())
		{
			return null;
		}

		try
		{
			IBlockState state = getBlockState(paint);

			if (!isValidPaint(state))
			{
				return null;
			}

			ItemStack stack = getItemStack(state);

			if (stack.getItem() != paint.getItem() || stack.getMetadata() != paint.getMetadata())
			{
				return null;
			}

			return stack;
		}
		catch (Exception ex)
		{
			return null;
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
		ItemStack stack1 = getValidPaint(paint);

		if (stack1 == null)
		{
			return false;
		}

		stack.setTagInfo("paint", stack1.serializeNBT());
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
			tooltip.add(I18n.format("gui.none"));
		}
		else
		{
			tooltip.add(paint.getDisplayName());
		}
	}
}