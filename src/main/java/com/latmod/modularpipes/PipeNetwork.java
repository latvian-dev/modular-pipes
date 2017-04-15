package com.latmod.modularpipes;

import com.latmod.modularpipes.block.BlockPipe;
import com.latmod.modularpipes.tile.TilePipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeNetwork
{
    private static final List<TilePipe> TEMP_LIST = new ArrayList<>();
    public static final Collection<BlockPos> TEMP_POS_SET = new HashSet<>();

    public static List<TilePipe> findPipes(TilePipe source, boolean newList)
    {
        List<TilePipe> list;

        if(newList)
        {
            list = new ArrayList<>();
        }
        else
        {
            TEMP_LIST.clear();
            list = TEMP_LIST;
        }

        TEMP_POS_SET.clear();
        TEMP_POS_SET.add(source.getPos());

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            findPipes0(source.getWorld(), source.getPos().offset(facing), facing.getOpposite(), list);
        }

        return list;
    }

    public static void findPipes0(World world, BlockPos pos, EnumFacing from, Collection<TilePipe> pipes)
    {
        IBlockState state = world.getBlockState(pos);

        if(!(state.getBlock() instanceof BlockPipe) || TEMP_POS_SET.contains(pos))
        {
            return;
        }

        TEMP_POS_SET.add(pos);

        if(state.getBlock().hasTileEntity(state))
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TilePipe)
            {
                pipes.add((TilePipe) tileEntity);
            }
        }

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing != from)
            {
                findPipes0(world, pos.offset(facing), facing.getOpposite(), pipes);
            }
        }
    }
}