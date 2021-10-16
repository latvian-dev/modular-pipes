package dev.latvian.mods.modularpipes.item.module.item;

import dev.latvian.mods.modularpipes.FilterUtils;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class ItemExtractModule extends ItemHandlerModule {
	public int tick = 0;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (tick > 0) {
			nbt.putByte("Tick", (byte) tick);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		tick = nbt.getByte("Tick");
	}

	@Override
	public void updateModule() {
		tick++;

		if (tick >= 7) {
			if (extractItem()) {
				spawnParticle(ModularPipesCommon.EXPLOSION);
			}

			tick = 0;
		}
	}

	private boolean extractItem() {
		BlockEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, sideData.direction.getOpposite()).orElse(null);

		if (handler != null) {
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stack = handler.extractItem(slot, 1, true);

				if (!stack.isEmpty() && FilterUtils.check(filter, stack)) {
					ItemStack stack1 = insertItem(0, stack, false);

					if (stack1.getCount() != stack.getCount()) {
						handler.extractItem(slot, stack.getCount() - stack1.getCount(), false);
						return true;
					}
				}
			}
		}

		return false;
	}
}