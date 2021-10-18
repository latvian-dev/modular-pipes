package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class PipeSideData {
	public final PipeBlockEntity entity;
	public final Direction direction;
	public boolean connect;
	public PipeModule module;
	public boolean light;
	public boolean disabled;

	public PipeSideData(PipeBlockEntity e, Direction d) {
		entity = e;
		direction = d;
		connect = false;
		module = null;
		light = false;
		disabled = false;
	}

	public boolean shouldWrite() {
		return connect || module != null || light || disabled;
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

		if (disabled) {
			tag.putBoolean("Disabled", true);
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
		disabled = tag.getBoolean("Disabled");
	}

	private boolean updateConnection0() {
		if (!canConnect()) {
			return false;
		}

		BlockEntity blockEntity = entity.getLevel().getBlockEntity(entity.getBlockPos().relative(direction));

		if (blockEntity instanceof PipeBlockEntity) {
			return ((PipeBlockEntity) blockEntity).sideData[direction.getOpposite().get3DDataValue()].canConnect();
		} else if (blockEntity != null) {
			return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent() || blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
		}

		return false;
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

		// 8

		// 16

		return index;
	}

	public boolean canConnect() {
		return !disabled;
	}

	public void setDisabled(boolean d) {
		disabled = d;
		connect = updateConnection0();
		entity.sync();
	}
}
