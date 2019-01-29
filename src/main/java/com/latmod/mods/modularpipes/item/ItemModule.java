package com.latmod.mods.modularpipes.item;

import com.latmod.mods.modularpipes.item.module.PipeModule;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class ItemModule extends Item
{
	public final Supplier<PipeModule> supplier;

	public ItemModule(Supplier<PipeModule> s)
	{
		supplier = s;
	}

	@Override
	@Nullable
	public PipeModule initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		PipeModule module = supplier.get();
		module.stack = stack;
		return module;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("item.modularpipes.module.name"));
	}
}