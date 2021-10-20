package dev.latvian.mods.modularpipes.util;

import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class ServerPipeNetwork extends PipeNetwork implements INBTSerializable<CompoundTag> {
	public final List<ModularPipeBlockEntity> pipes;
	public long nextItemID = 0L;
	private boolean refresh = true;

	public ServerPipeNetwork(Level w) {
		super(w);
		pipes = new ArrayList<>();
	}

	public void pipeBroken(BlockPos pos) {
		for (PipeItem item : pipeItems.values()) {
			if (item.path != null && item.path.contains(pos)) {
				Block.popResource(level, pos, item.stack);
				item.ttl = 0;
			}
		}
	}

	@Override
	public void refresh() {
		refresh = true;
	}

	@Override
	public boolean canTick() {
		return refresh || !pipes.isEmpty() || super.canTick();
	}

	@Override
	public void actuallyTick(ProfilerFiller profiler) {
		super.actuallyTick(profiler);

		if (refresh) {
			pipes.clear();

			for (BlockEntity tileEntity : level.blockEntityList) {
				if (!tileEntity.isRemoved() && tileEntity instanceof ModularPipeBlockEntity) {
					pipes.add((ModularPipeBlockEntity) tileEntity);
				}
			}

			for (PipeBlockEntity pipe : pipes) {
				pipe.clearCache();
			}

			refresh = false;
		}

		if (!level.isClientSide() && !pipes.isEmpty()) {
			profiler.push("PipeTick");

			for (ModularPipeBlockEntity pipe : pipes) {
				pipe.tickPipe();
				pipe.sendUpdates();
			}

			profiler.pop();
		}
	}

	public PipeItem insert(ItemStack stack) {
		PipeItem item = new PipeItem(this, ++nextItemID, stack);
		pipeItems.put(item.id, item);
		return item;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putLong("NextItemID", nextItemID);

		ListTag itag = new ListTag();

		for (PipeItem item : pipeItems.values()) {
			itag.add(item.toTag());
		}

		tag.put("Items", itag);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		nextItemID = tag.getLong("NextItemID");

		pipeItems.clear();

		ListTag itag = tag.getList("Items", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < itag.size(); i++) {
			PipeItem pipeItem = new PipeItem(itag.getCompound(i));

			if (!pipeItem.stack.isEmpty()) {
				pipeItem.network = this;
				pipeItems.put(pipeItem.id, pipeItem);
			}
		}
	}
}
