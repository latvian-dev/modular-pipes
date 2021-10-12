package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.BasePipeBlock;
import dev.latvian.mods.modularpipes.block.PipeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class BasePipeBlockEntity extends TileBase {
	public boolean sync = false;
	public boolean invisible = false;
	private boolean changed = false;
	public List<PipeItem> items = new ArrayList<>(0);
	private PipeTier cachedTier;

	public BasePipeBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void writeData(CompoundTag nbt) {
		if (invisible) {
			nbt.putBoolean("Invisible", true);
		}

		if (!items.isEmpty()) {
			ListTag list = new ListTag();

			for (PipeItem item : items) {
				list.add(item.serializeNBT());
			}

			nbt.put("Items", list);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		invisible = nbt.getBoolean("Invisible");

		ListTag list = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		items = new ArrayList<>(list.size());

		for (int i = 0; i < list.size(); i++) {
			CompoundTag nbt1 = list.getCompound(i);
			PipeItem item = new PipeItem();
			item.deserializeNBT(nbt1);

			if (!item.stack.isEmpty()) {
				items.add(item);
			}
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
		changed = true;
		sync = true;
	}

	public final void sendUpdates() {
		if (changed) {
			super.setChanged();

			if (!level.isClientSide() && sync) {
				BlockState state = level.getBlockState(worldPosition);
				level.sendBlockUpdated(worldPosition, state, state, 11);
			}

			changed = false;
		}
	}

	public boolean canPipesConnect() {
		return true;
	}

	public boolean isConnected(Direction facing) {
		BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(facing));

		if (tileEntity instanceof BasePipeBlockEntity) {
			return canPipesConnect();
		}

		return false;
	}

	public void dropItems() {
		for (PipeItem item : items) {
			Block.popResource(level, worldPosition, item.stack);
		}
	}

	public PipeTier getTier() {
		if (cachedTier == null) {
			cachedTier = ((BasePipeBlock) getBlockState().getBlock()).tier;
		}

		return cachedTier;
	}
}