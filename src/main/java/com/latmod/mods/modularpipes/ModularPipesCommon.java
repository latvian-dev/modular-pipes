package com.latmod.mods.modularpipes;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
	public static final int EXPLOSION = 0;
	public static final int SPARK = 1;

	public void spawnParticle(BlockPos pos, @Nullable EnumFacing facing, int type)
	{
	}

	public int getPipeLightValue(IBlockAccess world)
	{
		return 0;
	}
}