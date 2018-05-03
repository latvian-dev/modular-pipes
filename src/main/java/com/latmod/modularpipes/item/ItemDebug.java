package com.latmod.modularpipes.item;

import com.latmod.modularpipes.data.IPipe;
import com.latmod.modularpipes.data.Link;
import com.latmod.modularpipes.data.Node;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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

		if (state.getBlock() instanceof IPipe)
		{
			if (worldIn.isRemote)
			{
				return EnumActionResult.SUCCESS;
			}

			PipeNetwork network = PipeNetwork.get(worldIn);

			Node node = network.getNode(pos);
			if (node != null)
			{
				player.sendMessage(new TextComponentString("- Debug data @ " + node + ":")); //LANG
				player.sendMessage(new TextComponentString("Node with " + node.linkedWith.size() + " links")); //LANG
			}
			else
			{
				player.sendMessage(new TextComponentString("- Debug data @ [" + pos.getX() + ',' + pos.getY() + ',' + pos.getZ() + "]:")); //LANG
			}

			for (Link link : network.getLinks())
			{
				if (link.contains(pos, node != null))
				{
					player.sendMessage(new TextComponentString("Link " + link)); //LANG
				}
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (worldIn.isRemote)
		{
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		PipeNetwork network = PipeNetwork.get(worldIn);
		if (playerIn.isSneaking())
		{
			network.getNodes().forEach(Node::clearCache);
			network.items.values().forEach(TransportedItem::setRemoved);
			network.server().markDirty();
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (worldIn == null)
		{
			return;
		}

		tooltip.add("Network " + worldIn.provider.getDimension() + ":");
		tooltip.add("Total Items: " + PipeNetwork.get(worldIn).items.size());
	}
}