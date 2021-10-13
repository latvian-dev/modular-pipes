package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.BasePipeBlock;
import dev.latvian.mods.modularpipes.block.PipeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
public abstract class BasePipeBlockEntity extends BlockEntity {
	private boolean sync = false;
	public boolean invisible = false;
	private boolean changed = false;
	public List<PipeItem> items = new ArrayList<>(0);
	private PipeTier cachedTier;
	private int connections = -1;

	public BasePipeBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void writeData(CompoundTag nbt) {
		if (invisible) {
			nbt.putBoolean("Invisible", true);
		}

		if (connections != -1) {
			nbt.putInt("Connections", connections);
		}

		if (!items.isEmpty()) {
			ListTag list = new ListTag();

			for (PipeItem item : items) {
				list.add(item.serializeNBT());
			}

			nbt.put("Items", list);
		}
	}

	public void readData(CompoundTag nbt) {
		invisible = nbt.getBoolean("Invisible");
		connections = nbt.contains("Connections") ? nbt.getInt("Connections") : -1;

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

	@Override
	public void clearCache() {
		super.clearCache();
		connections = -1;
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		writeData(nbt);
		return super.save(nbt);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt) {
		super.load(state, nbt);
		readData(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		load(state, tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return new ClientboundBlockEntityDataPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		int c0 = connections;
		readData(packet.getTag());

		if (c0 != connections) {
			sync();
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
		if (getTier() != PipeTier.BASIC) {
			item.pos += item.speed;
			return;
		}

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
	}

	public void sync() {
		setChanged();
		sync = true;
	}

	public final void sendUpdates() {
		if (changed) {
			level.blockEntityChanged(worldPosition, this);

			if (sync) {
				BlockState state = level.getBlockState(worldPosition);
				level.sendBlockUpdated(worldPosition, state, state, 11);
			}

			changed = false;
		}
	}

	public boolean canPipesConnect() {
		return true;
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

	public int getConnections() {
		if (connections == -1) {
			connections = 0;

			for (int face = 0; face < 6; face++) {
				connections |= (updateConnection(face) & 3) << (face * 2);
			}

			sync();
		}

		return connections;
	}

	public int updateConnection(int face) {
		BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(Direction.from3DDataValue(face)));

		if (tileEntity instanceof BasePipeBlockEntity) {
			return canPipesConnect() ? 1 : 0;
		}

		return 0;
	}

	public int getConnection(int face) {
		return (getConnections() >> (face * 2)) & 3;
	}
}