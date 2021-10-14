package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PipeSideData {
	public final PipeBlockEntity entity;
	public final Direction direction;
	public boolean connect;
	public PipeModule module;
	public boolean light;

	public PipeSideData(PipeBlockEntity e, Direction d) {
		entity = e;
		direction = d;
		connect = false;
		module = null;
		light = false;
	}

	public CompoundTag write(CompoundTag tag) {
		if (connect) {
			tag.putBoolean("Connect", true);
		}

		if (module != null) {
			CompoundTag nbt1 = module.moduleItem.serializeNBT();
			CompoundTag nbt2 = new CompoundTag();
			module.writeData(nbt2);

			if (!nbt2.isEmpty()) {
				nbt1.put("ModuleData", nbt2);
			}

			tag.put("Module", nbt1);
		}

		if (light) {
			tag.putBoolean("Light", true);
		}

		return tag;
	}

	public void read(CompoundTag tag) {
		connect = tag.getBoolean("Connect");

		if (tag.contains("Module")) {
			CompoundTag nbt1 = tag.getCompound("Module");
			ItemStack stack = ItemStack.of(nbt1);
			module = stack.getCapability(PipeModule.CAP, null).orElse(null);

			if (module != null) {
				module.sideData = this;
				module.moduleItem = stack;
				module.readData(nbt1.getCompound("ModuleData"));
			}
		} else {
			module = null;
		}

		light = tag.getBoolean("Light");
	}

	private boolean updateConnection0() {
		BlockEntity tileEntity = entity.getLevel().getBlockEntity(entity.getBlockPos().relative(direction));

		return tileEntity instanceof PipeBlockEntity;
	}

	public void updateConnection() {
		int b = getModelIndex();
		connect = updateConnection0();

		if (b != getModelIndex()) {
			entity.sync();
		}
	}

	public boolean extendShape() {
		return connect || light || module != null;
	}

	public int getModelIndex() {
		int index = 0;

		if (connect || module != null) {
			index |= 1;
		}

		if (module != null) {
			index |= 2;
		}

		if (light) {
			index |= 4;
		}

		return index;
	}
}
