package com.latmod.modularpipes.item;

import com.feed_the_beast.ftblib.lib.util.LangKey;
import com.latmod.modularpipes.data.IModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemModule extends ItemMPBase implements IModule
{
	public static final LangKey DESC = LangKey.of("item.modularpipes.module.desc");
	public static final LangKey CLEARED_DATA = LangKey.of("item.modularpipes.module.cleared_data");

	public ItemModule(String id)
	{
		super(id);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (playerIn.isSneaking() && (stack.hasTagCompound() && stack.getTagCompound().hasKey("Module")))
		{
			stack.getTagCompound().removeTag("Module");

			if (stack.getTagCompound().hasNoTags())
			{
				stack.setTagCompound(null);
			}

			CLEARED_DATA.sendMessage(playerIn);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(DESC.translate());
	}
}