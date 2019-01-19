package com.latmod.modularpipes.item;

import com.latmod.modularpipes.block.PipeSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemPainter extends Item
{
	public ItemPainter()
	{
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		PipeSkin skin = stack.hasTagCompound() ? PipeSkin.byName(stack.getTagCompound().getString("skin")) : PipeSkin.NONE;
		ArrayList<PipeSkin> skins = new ArrayList<>(PipeSkin.MAP.values());
		PipeSkin newSkin = player.isSneaking() ? PipeSkin.NONE : skins.get((skins.indexOf(skin) + 1) % skins.size());
		stack.setTagInfo("skin", new NBTTagString(newSkin.name));

		if (world.isRemote)
		{
			player.sendStatusMessage(new TextComponentTranslation(newSkin.translationKey), true);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format((stack.hasTagCompound() ? PipeSkin.byName(stack.getTagCompound().getString("skin")) : PipeSkin.NONE).translationKey));
	}
}