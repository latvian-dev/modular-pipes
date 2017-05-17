package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author LatvianModder
 */
public class BlockPipeSpeed extends BlockPipeBasic
{
    public BlockPipeSpeed(String id)
    {
        super(id, MapColor.GOLD);
    }

    @Override
    public double getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return ModularPipesConfig.SPEED_PIPE_BOOST.getAsDouble();
    }

    @Override
    public IBlockState getNodeState()
    {
        return ModularPipesItems.PIPE_MODULAR.getDefaultState().withProperty(BlockModularPipe.TIER, BlockModularPipe.Tier.NODE_SPEED);
    }
}