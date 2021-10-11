package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.BasePipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasePipeBlockEntity extends TileBase {
	public boolean sync = false;
	public boolean invisible = false;
	private boolean isDirty = false;

	public BasePipeBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void writeData(CompoundTag nbt) {
		if (invisible) {
			nbt.putBoolean("invisible", true);
		}
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(BasePipeBlock.PIPE, this).build();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return super.getCapability(cap, side);
	}

	@Override
	public void readData(CompoundTag nbt) {
		invisible = nbt.getBoolean("invisible");
	}

	@Override
	public void setRemoved() {
		if (hasLevel()) {
			PipeNetwork.get(getLevel()).refresh();
		}

		super.setRemoved();
	}

	@Override
	public void setLevelAndPosition(Level world, BlockPos pos) {
		super.setLevelAndPosition(world, pos);

		if (hasLevel()) {
			PipeNetwork.get(getLevel()).refresh();
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
	public void setChanged() {
		isDirty = true;
		sync = true;
	}

	public final void sendUpdates() {
		if (isDirty) {
			super.setChanged();

			if (!level.isClientSide() && sync) {
				BlockState state = level.getBlockState(worldPosition);
				level.sendBlockUpdated(worldPosition, state, state, 11);
			}

			isDirty = false;
		}
	}

	public boolean canPipesConnect() {
		return true;
	}

	public boolean isConnected(Direction facing) {
		return false;
	}
}