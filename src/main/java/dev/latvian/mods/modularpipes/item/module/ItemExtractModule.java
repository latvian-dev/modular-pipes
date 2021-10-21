package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.modularpipes.client.PipeParticle;
import dev.latvian.mods.modularpipes.util.ServerPipeNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class ItemExtractModule extends PipeModule {
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
	public void updateModule(ServerPipeNetwork network) {
		tick++;

		if (tick >= 8) {
			if (extractItem(network)) {
				spawnParticle(PipeParticle.DEBUG_EXPLOSION);
			}

			tick = 0;
		}
	}

	private boolean extractItem(ServerPipeNetwork network) {
		BlockEntity tile = getFacingTile();
		IItemHandler handler = tile == null ? null : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, sideData.direction.getOpposite()).orElse(null);

		if (handler != null) {
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stack = handler.extractItem(slot, 1, true);

				if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack) && sideData.insertItem(network, stack, 3)) {
					handler.extractItem(slot, 1, false);
					return true;
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

			if (!player.level.isClientSide()) {
				player.displayClientMessage(new TextComponent("Filter changed to " + filter.getDisplayName()), true); //LANG
				refreshNetwork();
			}
		}

		return true;
	}
}