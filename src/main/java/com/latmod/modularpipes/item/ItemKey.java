package com.latmod.modularpipes.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public final class ItemKey
{
	public static final ItemKey EMPTY = new ItemKey(ItemStack.EMPTY);

	public static ItemKey of(ItemStack stack)
	{
		return stack.isEmpty() ? EMPTY : new ItemKey(stack);
	}

	public final Item item;
	public final int metadata;
	public final NBTTagCompound nbt;

	private ItemKey(ItemStack stack)
	{
		item = stack.getItem();
		metadata = stack.getHasSubtypes() ? stack.getMetadata() : OreDictionary.WILDCARD_VALUE;
		nbt = item.getNBTShareTag(stack);
	}

	public int hashCode()
	{
		return item.hashCode() * 31 + Objects.hashCode(nbt) + metadata;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o instanceof ItemKey)
		{
			ItemKey key = (ItemKey) o;
			return item == key.item && metadata == key.metadata && Objects.equals(nbt, key.nbt);
		}

		return false;
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder(item.getRegistryName().toString());

		if (metadata != OreDictionary.WILDCARD_VALUE)
		{
			builder.append('@');
			builder.append(metadata);
		}

		if (nbt != null)
		{
			builder.append('+');
			builder.append(nbt);
		}

		return builder.toString();
	}
}