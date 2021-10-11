package dev.latvian.mods.modularpipes.item.module.item;

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

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * @author LatvianModder
 */
public class ItemStorageModule extends SidedPipeModule {
	public static final Comparator<ItemStorageModule> COMPARATOR = (o1, o2) ->
	{
		int i = Integer.compare(o2.priority, o1.priority);

		if (i == 0) {
			return Boolean.compare(o1.filter.isEmpty(), o2.filter.isEmpty());
		}

		return i;
	};

	public ItemStack filter = ItemStack.EMPTY;
	public int priority = 0;
	private BlockEntity tileEntity = null;

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (!filter.isEmpty()) {
			nbt.put("filter", filter.serializeNBT());
		}

		if (priority != 0) {
			nbt.putInt("priority", priority);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		filter = ItemStack.of(nbt.getCompound("filter"));

		if (filter.isEmpty()) {
			filter = ItemStack.EMPTY;
		}

		priority = nbt.getInt("priority");
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

	@Override
	public void clearCache() {
		tileEntity = null;
	}

	@Nullable
	public IItemHandler getItemHandler() {
		if (tileEntity == null || tileEntity.isRemoved()) {
			tileEntity = getFacingTile();
		}

		return tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);
	}
}