package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ModuleItem extends Item {
	public final Supplier<PipeModule> supplier;

	public ModuleItem(Supplier<PipeModule> s, Properties properties) {
		super(properties);
		supplier = s;
	}

	@Override
	@Nullable
	public PipeModule initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		PipeModule module = supplier.get();
		module.moduleItem = stack;
		return module;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		list.add(new TranslatableComponent("item.modularpipes.module").withStyle(ChatFormatting.GRAY));
	}
}