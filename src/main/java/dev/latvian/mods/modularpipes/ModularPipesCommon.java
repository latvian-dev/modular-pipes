package dev.latvian.mods.modularpipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon {
	public static final int EXPLOSION = 0;
	public static final int SPARK = 1;

	public void init() {
	}

	public void spawnParticle(BlockPos pos, @Nullable Direction facing, int type) {
	}

	public int getPipeLightValue(BlockGetter level) {
		return 0;
	}
}