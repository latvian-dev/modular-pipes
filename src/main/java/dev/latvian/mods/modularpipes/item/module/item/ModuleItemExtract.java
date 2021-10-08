package dev.latvian.mods.modularpipes.item.module.item;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class ModuleItemExtract extends ModuleItemHandler {
	public int tick = 0;

	@Override
	public void writeData(CompoundNBT nbt) {
		super.writeData(nbt);

		if (tick > 0) {
			nbt.putByte("tick", (byte) tick);
		}
	}

	@Override
	public void readData(CompoundNBT nbt) {
		super.readData(nbt);
		tick = nbt.getByte("tick");
	}

	@Override
	public boolean canUpdate() {
		return true;
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
		TileEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);

		if (handler != null) {
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stack = handler.extractItem(slot, 1, true);

				if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack)) {
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