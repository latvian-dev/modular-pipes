package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * @author LatvianModder
 */
public class ItemExtractModule extends PipeModule implements IItemHandler {
	public ItemStack filter = ItemStack.EMPTY;
	public int tick = 0;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (!filter.isEmpty()) {
			nbt.put("Filter", filter.serializeNBT());
		}

		if (tick > 0) {
			nbt.putByte("Tick", (byte) tick);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);

		if (nbt.contains("Filter")) {
			filter = ItemStack.of(nbt.getCompound("Filter"));
		} else {
			filter = ItemStack.EMPTY;
		}

		tick = nbt.getByte("Tick");
	}

	@Override
	public boolean isThisCapability(Capability<?> capability) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
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

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		/*
		for (ItemStorageModule module : getStorageModules()) {
			if (ItemFiltersAPI.filter(module.filter, stack)) {
				IItemHandler handler1 = module.getItemHandler();

				if (handler1 != null) {
					stack = ItemHandlerHelper.insertItem(handler1, stack, simulate);

					if (stack.isEmpty()) {
						return ItemStack.EMPTY;
					}
				}
			}
		}
		 */

		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		/*
		for (ItemStorageModule module : getStorageModules()) {
			if (ItemFiltersAPI.filter(module.filter, stack)) {
				return true;
			}
		}
		 */
		return false;
	}

	@Override
	public boolean onModuleRightClick(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.isEmpty()) {
			if (!player.level.isClientSide()) {
				player.displayClientMessage(new TextComponent("Filter: " + filter.getDisplayName()), true); //LANG
			}
		} else {
			filter = ItemHandlerHelper.copyStackWithSize(stack, 1);

			if (!player.level.isClientSide()) {
				player.displayClientMessage(new TextComponent("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}