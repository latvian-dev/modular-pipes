package dev.latvian.mods.modularpipes.gui;

import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK1;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ContainerPipeModular extends Container {
	public final PlayerEntity player;
	public final TilePipeModularMK1 pipe;
	public final int facing;

	public ContainerPipeModular(int id, PlayerEntity ep, TilePipeModularMK1 p, int f) {
		super(ModularPipesContainers.PIPE_MODULAR, id);
		player = ep;
		pipe = p;
		facing = f;
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				addSlot(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 36 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			addSlot(new Slot(player.inventory, l, 8 + l * 18, 94));
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean enchantItem(PlayerEntity player, int id) {
		return false;
	}
}