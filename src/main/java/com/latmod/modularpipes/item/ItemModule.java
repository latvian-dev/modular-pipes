package com.latmod.modularpipes.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ItemModule extends ItemBase
{
    public ItemModule(String id)
    {
        super(id);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(playerIn.isSneaking() && (stack.hasTagCompound() && stack.getTagCompound().hasKey("Module")))
        {
            stack.getTagCompound().removeTag("Module");

            if(stack.getTagCompound().hasNoTags())
            {
                stack.setTagCompound(null);
            }

            playerIn.sendMessage(new TextComponentString("Cleared Module Data")); //TODO: Lang
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}