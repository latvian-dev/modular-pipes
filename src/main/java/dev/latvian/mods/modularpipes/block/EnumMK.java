package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK1;
import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK2;
import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK3;
import net.minecraft.util.IStringSerializable;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum EnumMK implements IStringSerializable {
	MK1("mk1", TilePipeModularMK1::new, 1),
	MK2("mk2", TilePipeModularMK2::new, 4),
	MK3("mk3", TilePipeModularMK3::new, 9);

	public static final EnumMK[] VALUES = values();
	public final Supplier<? extends TilePipeModularMK1> tileEntity;
	private final String name;
	public int maxModules;

	EnumMK(String n, Supplier<? extends TilePipeModularMK1> te, int m) {
		name = n;
		tileEntity = te;
		maxModules = m;
	}

	@Override
	public String getName() {
		return name;
	}
}