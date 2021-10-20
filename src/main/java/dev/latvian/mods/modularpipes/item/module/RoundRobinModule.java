package dev.latvian.mods.modularpipes.item.module;

import net.minecraft.nbt.CompoundTag;

/**
 * @author LatvianModder
 */
public class RoundRobinModule extends PipeModule {
	public int counter = 0;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);
		nbt.putByte("Counter", (byte) counter);
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		counter = nbt.getByte("Counter");
	}
}