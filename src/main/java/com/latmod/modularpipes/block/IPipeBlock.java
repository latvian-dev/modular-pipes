package com.latmod.modularpipes.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author LatvianModder
 */
public interface IPipeBlock
{
	boolean hasPipeConnection(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing);
}