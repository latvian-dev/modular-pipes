package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeModularMK1;
import com.latmod.modularpipes.tile.TilePipeModularMK2;
import com.latmod.modularpipes.tile.TilePipeModularMK3;
import net.minecraft.util.IStringSerializable;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum EnumMK implements IStringSerializable
{
	MK1("mk1", TilePipeModularMK1::new),
	MK2("mk2", TilePipeModularMK2::new),
	MK3("mk3", TilePipeModularMK3::new);

	public static final EnumMK[] VALUES = values();

	private final String name;
	public final Supplier<? extends TilePipeModularMK1> tileEntity;

	EnumMK(String n, Supplier<? extends TilePipeModularMK1> te)
	{
		name = n;
		tileEntity = te;
	}

	@Override
	public String getName()
	{
		return name;
	}
}