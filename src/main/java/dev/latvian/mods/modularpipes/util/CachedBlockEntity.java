package dev.latvian.mods.modularpipes.util;

import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class CachedBlockEntity {
	public static final CachedBlockEntity NONE = new CachedBlockEntity(null, 0);

	public final BlockEntity blockEntity;
	public final int distance;

	public CachedBlockEntity(@Nullable BlockEntity t, int d) {
		blockEntity = t;
		distance = d;
	}
}