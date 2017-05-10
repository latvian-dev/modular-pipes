package com.latmod.modularpipes.item;

import com.latmod.modularpipes.client.ClientPipeNetwork;
import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemDebug extends ItemMPBase
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

            TransportedItem item = new TransportedItem(PipeNetwork.get(worldIn));
            item.stack = ItemHandlerHelper.copyStackWithSize(player.getHeldItem(EnumHand.OFF_HAND), 1);

            if(item.stack.isEmpty())
            {
                item.stack = new ItemStack(Blocks.LOG);
            }

            item.path.add(pos);
            item.path.add(pos.offset(EnumFacing.EAST, 3));
            item.addToNetwork();
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(worldIn.isRemote)
        {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if(playerIn.isSneaking())
        {
            PipeNetwork.get(worldIn).items.values().forEach(value -> value.action = TransportedItem.Action.REMOVE);
        }
        else
        {
            PipeNetwork.get(worldIn).server().playerLoggedIn(playerIn);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.add("Network " + playerIn.world.provider.getDimension() + ":");
        tooltip.add("Total Items: " + ClientPipeNetwork.get().items.size());
    }
}