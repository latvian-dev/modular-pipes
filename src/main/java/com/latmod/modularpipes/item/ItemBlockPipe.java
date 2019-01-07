package com.latmod.modularpipes.item;

import com.latmod.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ItemBlockPipe extends ItemBlock
{
	public ItemBlockPipe(Block block)
	{
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
	{
		if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
		{
			TileEntity tileEntity = world.getTileEntity(pos.offset(side.getOpposite()));

			if (tileEntity instanceof TilePipeBase)
			{
				TileEntity pipe = world.getTileEntity(pos);

				if (pipe instanceof TilePipeBase)
				{
					((TilePipeBase) pipe).skin = ((TilePipeBase) tileEntity).skin;
					pipe.markDirty();
				}
			}

			return true;
		}

		return false;
	}
}