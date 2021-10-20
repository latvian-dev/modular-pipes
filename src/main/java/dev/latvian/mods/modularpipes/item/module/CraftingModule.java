package dev.latvian.mods.modularpipes.item.module;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class CraftingModule extends PipeModule {
	@Override
	public Component canInsert(Player player, InteractionHand hand) {
		return new TextComponent("WIP!");
	}
}