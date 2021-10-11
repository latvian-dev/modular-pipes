package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK1BlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK2BlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK3BlockEntity;
import net.minecraft.util.StringRepresentable;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum EnumMK implements StringRepresentable {
	MK1("mk1", ModularPipeMK1BlockEntity::new, 1),
	MK2("mk2", ModularPipeMK2BlockEntity::new, 4),
	MK3("mk3", ModularPipeMK3BlockEntity::new, 9);

	public static final EnumMK[] VALUES = values();
	public final Supplier<? extends ModularPipeMK1BlockEntity> tileEntity;
	private final String name;
	public int maxModules;

	EnumMK(String n, Supplier<? extends ModularPipeMK1BlockEntity> te, int m) {
		name = n;
		tileEntity = te;
		maxModules = m;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}