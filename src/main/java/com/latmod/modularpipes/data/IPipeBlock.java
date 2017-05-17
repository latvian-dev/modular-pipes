package com.latmod.modularpipes.data;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public interface IPipeBlock
{
    default NodeType getNodeType(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return NodeType.NONE;
    }

    default double getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return 1D;
    }

    default boolean superBoost(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return false;
    }

    default boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing)
    {
        return true;
    }

    default EnumFacing getPipeFacing(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing source)
    {
        return source;
    }

    default EnumFacing getItemDirection(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing source)
    {
        return getPipeFacing(world, pos, state, source);
    }

    default void onItemEntered(World world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing facing)
    {
    }

    default void onItemExited(World world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing facing)
    {
    }
}