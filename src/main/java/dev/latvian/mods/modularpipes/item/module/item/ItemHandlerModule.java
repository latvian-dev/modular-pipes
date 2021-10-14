package dev.latvian.mods.modularpipes.item.module.item;

import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeSideData;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemHandlerModule extends PipeModule implements IItemHandler {
	public ItemStack filter = ItemStack.EMPTY;
	private List<ItemStorageModule> storageModules = null;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (!filter.isEmpty()) {
			nbt.put("Filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		filter = ItemStack.of(nbt.getCompound("Filter"));

		if (filter.isEmpty()) {
			filter = ItemStack.EMPTY;
		}
	}

	public List<ItemStorageModule> getStorageModules() {
		if (storageModules == null) {
			storageModules = new ArrayList<>(2);

			for (ModularPipeBlockEntity pipe1 : ((ModularPipeBlockEntity) sideData.entity).getPipeNetwork()) {
				for (PipeSideData data : pipe1.sideData) {
					if (data.module instanceof ItemStorageModule) {
						storageModules.add((ItemStorageModule) data.module);
					}
				}
			}

			if (storageModules.size() > 1) {
				storageModules.sort(ItemStorageModule.COMPARATOR);
			}
		}

		return storageModules;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		storageModules = null;
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
		for (ItemStorageModule module : getStorageModules()) {
			if (ItemFiltersAPI.filter(module.filter, stack)) {
				return true;
			}
		}
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