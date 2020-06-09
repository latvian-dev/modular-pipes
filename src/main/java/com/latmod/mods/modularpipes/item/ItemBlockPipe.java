package com.latmod.mods.modularpipes.item;

import com.latmod.mods.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class ItemBlockPipe extends BlockItem
{
	public ItemBlockPipe(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockItemUseContext context, BlockState state)
	{
		if (super.placeBlock(context, state))
		{
			World world = context.getWorld();
			BlockPos pos = context.getPos();
			Direction side = context.getFace();
			TileEntity tileEntity = world.getTileEntity(pos.offset(side.getOpposite()));

			if (tileEntity instanceof TilePipeBase)
			{
				TileEntity pipe = world.getTileEntity(pos);

				if (pipe instanceof TilePipeBase)
				{
					((TilePipeBase) pipe).paint = ((TilePipeBase) tileEntity).paint;
					pipe.markDirty();
				}
			}

			return true;
		}

		return false;
	}
}