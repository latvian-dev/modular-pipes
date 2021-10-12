package dev.latvian.mods.modularpipes.item.module.item;

import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import dev.latvian.mods.modularpipes.block.entity.BaseModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import dev.latvian.mods.modularpipes.item.module.SidedPipeModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemInsertModule extends SidedPipeModule {
	public ItemStack filter = ItemStack.EMPTY;
	public int tick = 0;
	private List<ItemStorageModule> storageModules = null;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (tick > 0) {
			nbt.putByte("tick", (byte) tick);
		}

		if (!filter.isEmpty()) {
			nbt.put("filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		tick = nbt.getByte("tick");

		filter = ItemStack.of(nbt.getCompound("filter"));

		if (filter.isEmpty()) {
			filter = ItemStack.EMPTY;
		}
	}

	public List<ItemStorageModule> getStorageModules() {
		if (storageModules == null) {
			storageModules = new ArrayList<>(2);

			for (BaseModularPipeBlockEntity pipe1 : pipe.getPipeNetwork()) {
				for (PipeModule module : pipe1.modules) {
					if (module instanceof ItemStorageModule) {
						storageModules.add((ItemStorageModule) module);
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
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateModule() {
		if (filter.isEmpty()) {
			return;
		}

		tick++;

		if (tick >= 7) {
			if (insertItem()) {
				spawnParticle(ModularPipesCommon.EXPLOSION);
			}

			tick = 0;
		}
	}

	private boolean insertItem() {
		BlockEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);

		if (handler != null) {
			for (ItemStorageModule module : getStorageModules()) {
				IItemHandler handler1 = module.getItemHandler();

				if (handler1 != null) {
					for (int i = 0; i < handler1.getSlots(); i++) {
						ItemStack stack = handler1.extractItem(i, 1, true);

						if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack)) {
							ItemStack stack1 = ItemHandlerHelper.insertItem(handler, stack, false);

							if (stack.getCount() != stack1.getCount()) {
								handler1.extractItem(i, stack.getCount() - stack1.getCount(), false);
								return true;
							}
						}
					}
				}
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
			pipe.setChanged();

			if (!player.level.isClientSide()) {
				player.displayClientMessage(new TextComponent("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}