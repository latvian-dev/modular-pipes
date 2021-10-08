package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemModule extends Item {
	public final Supplier<PipeModule> supplier;

	public ItemModule(Supplier<PipeModule> s, Properties properties) {
		super(properties);
		supplier = s;
	}

	@Override
	@Nullable
	public PipeModule initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		PipeModule module = supplier.get();
		module.moduleItem = stack;
		return module;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent("item.modularpipes.module.name"));
	}
}