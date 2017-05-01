package com.latmod.modularpipes.item;

import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ItemDebug extends ItemBase
{
    public ItemDebug(String id)
    {
        super(id);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = worldIn.getBlockState(pos);

        if(state.getBlock() instanceof IPipeBlock)
        {
            if(worldIn.isRemote)
            {
                return EnumActionResult.SUCCESS;
            }

            TransportedItem item = new TransportedItem();
            item.stack = new ItemStack(Items.DIAMOND_SWORD);
            item.path.add(pos);
            item.path.add(pos);
            PipeNetwork.get(worldIn).addItem(item);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(playerIn.isSneaking())
        {
            PipeNetwork.get(worldIn).sync(true);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}