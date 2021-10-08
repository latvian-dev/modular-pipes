package dev.latvian.mods.modularpipes.gui.painter;

import dev.latvian.mods.modularpipes.gui.ModularPipesContainers;
import dev.latvian.mods.modularpipes.item.ItemPainter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ContainerPainter extends Container {
	public final PlayerEntity player;
	public final ItemStack stack;

	public ContainerPainter(int id, PlayerEntity p, ItemStack is) {
		super(ModularPipesContainers.PAINTER, id);
		player = p;
		stack = is;

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				addSlot(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 36 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			if (player.inventory.getStackInSlot(l) != stack) {
				addSlot(new Slot(player.inventory, l, 8 + l * 18, 94));
			} else {
				addSlot(new Slot(player.inventory, l, 8 + l * 18, 94) {
					@Override
					public boolean canTakeStack(PlayerEntity player) {
						return false;
					}
				});
			}
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemPainter.setPaint(stack, inventorySlots.get(index).getStack());
		detectAndSendChanges();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean enchantItem(PlayerEntity player, int id) {
		return ItemPainter.setPaint(stack, player.inventory.getItemStack());
	}
}