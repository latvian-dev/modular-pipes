package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author LatvianModder
 */
public class WrenchItem extends Item {
	public static final ToolAction WRENCH_ACTION = ToolAction.get("wrench");

	public WrenchItem() {
		super(new Properties().tab(ModularPipes.TAB).stacksTo(1));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction action) {
		return action == WRENCH_ACTION;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		list.add(new TranslatableComponent("item.modularpipes.wrench.info").withStyle(ChatFormatting.GRAY));
	}
}