package com.latmod.modularpipes.data;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class Node
{
    public final PipeNetwork network;
    public final BlockPos pos;
    public final Collection<Link> linkedWith;
    private TileEntity tile;

    public Node(PipeNetwork n, BlockPos p)
    {
        network = n;
        pos = p;
        linkedWith = new HashSet<>();
    }

    @Nullable
    public TileEntity getTile()
    {
        if(tile == null || tile.isInvalid())
        {
            tile = network.world.getTileEntity(pos);
        }

        return tile;
    }

    public void clearCache()
    {
        tile = null;
    }
}