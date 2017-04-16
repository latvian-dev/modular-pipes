package com.latmod.modularpipes.util;

import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class BlockDimPos
{
    public final BlockPos pos;
    public final int dim;

    public BlockDimPos(BlockPos p, int d)
    {
        pos = p;
        dim = d;
    }

    public int hashCode()
    {
        return dim * 31 + pos.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == this)
        {
            return true;
        }
        else if(o == null || o.getClass() != BlockDimPos.class)
        {
            return false;
        }

        BlockDimPos dpos = (BlockDimPos) o;
        return dpos.dim == dim && dpos.pos.getX() == pos.getX() && dpos.pos.getY() == pos.getY() && dpos.pos.getZ() == pos.getZ();
    }
}