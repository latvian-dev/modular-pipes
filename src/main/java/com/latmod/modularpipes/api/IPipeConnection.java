package com.latmod.modularpipes.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author LatvianModder
 */
public interface IPipeConnection
{
    boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing);
}