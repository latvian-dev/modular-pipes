package com.latmod.modularpipes.block;

import com.latmod.modularpipes.api.IPipeConnection;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author LatvianModder
 */
public class BlockController extends BlockBase implements IPipeConnection
{
    public BlockController(String id)
    {
        super(id, Material.IRON, MapColor.BLUE);
    }

    @Override
    public boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing)
    {
        return true;
    }
}