package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.TransportPipeBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * @author LatvianModder
 */
public class TransportPipeBlock extends BasePipeBlock {
	public TransportPipeBlock() {
		super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(0.35f).sound(SoundType.METAL));
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new TransportPipeBlockEntity();
	}

	/*
	private void updatePipe(BlockState state, World world, BlockPos pos, int loop) {
		if (loop > 1) {
			return;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TilePipeTransport) {
			TilePipeTransport pipe = (TilePipeTransport) tileEntity;
			pipe.end1 = null;
			pipe.end2 = null;

			int count = 0;

			for (Direction facing : Direction.values()) {
				TileEntity tileEntity1 = world.getTileEntity(pos.offset(facing));

				if (tileEntity1 instanceof TilePipeBase && pipe.canPipesConnect(((TilePipeBase) tileEntity1).paint)) {
					if (pipe.end1 == null) {
						pipe.end1 = facing;
					} else if (pipe.end2 == null) {
						pipe.end2 = facing;
					}

					count++;
				}
			}

			if (count > 2) {
				pipe.end1 = null;
				pipe.end2 = null;
			}

			world.notifyBlockUpdate(pos, state, state, 8);

			if (count > 2) {
				for (Direction facing : Direction.values()) {
					BlockPos pos1 = pos.offset(facing);
					BlockState state1 = world.getBlockState(pos1);

					if (state1.getBlock() instanceof BlockPipeTransport) {
						((BlockPipeTransport) state1.getBlock()).updatePipe(state1, world, pos1, loop + 1);
					}
				}
			} else {
				if (pipe.end1 != null) {
					BlockPos pos1 = pos.offset(pipe.end1);
					world.notifyBlockUpdate(pos1, world.getBlockState(pos1), world.getBlockState(pos1), 8);
				}

				if (pipe.end2 != null) {
					BlockPos pos2 = pos.offset(pipe.end2);
					world.notifyBlockUpdate(pos2, world.getBlockState(pos2), world.getBlockState(pos2), 8);
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		updatePipe(state, worldIn, pos, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		updatePipe(state, world, pos, 0);
	}
	 */
}