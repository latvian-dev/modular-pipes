package dev.latvian.mods.modularpipes.block.entity;

import com.latmod.mods.itemfilters.api.IPaintable;
import com.latmod.mods.itemfilters.api.PaintAPI;
import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.BlockPipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class TilePipeBase extends TileBase implements IPaintable {
	public boolean sync = false;
	public int paint = 0;
	public boolean invisible = false;
	private boolean isDirty = false;

	public TilePipeBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void writeData(CompoundNBT nbt) {
		if (paint != 0) {
			nbt.putInt("paint", paint);
		}

		if (invisible) {
			nbt.putBoolean("invisible", true);
		}
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(BlockPipeBase.PIPE, this).build();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == PaintAPI.CAPABILITY ? thisOptional.cast() : super.getCapability(cap, side);
	}

	@Override
	public void readData(CompoundNBT nbt) {
		paint = nbt.getInt("paint");
		invisible = nbt.getBoolean("invisible");
	}

	@Override
	public void remove() {
		if (hasWorld()) {
			PipeNetwork.get(getWorld()).refresh();
		}

		super.remove();
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);

		if (hasWorld()) {
			PipeNetwork.get(getWorld()).refresh();
		}
	}

	public void moveItem(PipeItem item) {
		item.pos += Math.min(item.speed, 0.99F);
		float pipeSpeed = (float) ModularPipesConfig.pipes.base_speed;

		if (item.speed > pipeSpeed) {
			item.speed *= 0.99F;

			if (item.speed < pipeSpeed) {
				item.speed = pipeSpeed;
			}
		} else if (item.speed < pipeSpeed) {
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed) {
				item.speed = pipeSpeed;
			}
		}
	}

	@Override
	public void markDirty() {
		isDirty = true;
		sync = true;
	}

	public final void sendUpdates() {
		if (isDirty) {
			super.markDirty();

			if (!world.isRemote && sync) {
				BlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 11);
			}

			isDirty = false;
		}
	}

	public boolean canPipesConnect(int p) {
		return paint == p || paint == 0 || p == 0;
	}

	public boolean isConnected(Direction facing) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void paint(BlockState paintState, Direction facing, boolean all) {
		if (all) {
			HashSet<TilePipeBase> pipes = new HashSet<>();
			paintAll(this, paint, pipes, Block.getStateId(paintState));

			for (TilePipeBase pipe : pipes) {
				pipe.markDirty();
				BlockState state = world.getBlockState(pipe.getPos());
				state.getBlock().neighborChanged(state, world, pipe.getPos(), state.getBlock(), pipe.getPos().offset(facing), false);

				if (world.isRemote) {
					world.notifyBlockUpdate(pipe.getPos(), state, state, 11);
				}
			}

			PipeNetwork.get(world).refresh();
			return;
		}

		paint = Block.getStateId(paintState);
		markDirty();
		BlockState state = world.getBlockState(pos);
		state.getBlock().neighborChanged(state, world, pos, state.getBlock(), pos.offset(facing), false);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());

		if (world.isRemote) {
			world.notifyBlockUpdate(pos, state, state, 11);
		}

		PipeNetwork.get(world).refresh();
	}

	@Override
	public BlockState getPaint() {
		return Block.getStateById(paint);
	}

	private void paintAll(TilePipeBase pipe, int originalPaint, HashSet<TilePipeBase> visited, int paint) {
		if (pipe.paint == originalPaint) {
			pipe.paint = paint;
			visited.add(pipe);

			for (Direction facing : Direction.values()) {
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(facing));

				if (tileEntity instanceof TilePipeBase && !visited.contains(tileEntity)) {
					paintAll((TilePipeBase) tileEntity, originalPaint, visited, paint);
				}
			}
		}
	}
}