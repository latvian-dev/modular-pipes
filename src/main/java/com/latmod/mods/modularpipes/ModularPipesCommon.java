package com.latmod.mods.modularpipes;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	public static final int EXPLOSION = 0;
	public static final int SPARK = 1;

	public void spawnParticle(BlockPos pos, @Nullable Direction facing, int type)
	{
	}

	public int getPipeLightValue(IBlockReader world)
	{
		return 0;
	}
}