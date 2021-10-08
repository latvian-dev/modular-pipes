package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.block.entity.TilePipeBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
public class ItemBlockPipe extends BlockItem {
	public ItemBlockPipe(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (super.placeBlock(context, state)) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			Direction side = context.getClickedFace();
			BlockEntity tileEntity = level.getBlockEntity(pos.relative(side.getOpposite()));

			if (tileEntity instanceof TilePipeBase) {
				BlockEntity pipe = level.getBlockEntity(pos);

				if (pipe instanceof TilePipeBase) {
					((TilePipeBase) pipe).paint = ((TilePipeBase) tileEntity).paint;
					pipe.setChanged();
				}
			}

			return true;
		}

		return false;
	}
}