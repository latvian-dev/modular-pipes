package com.latmod.modularpipes.data;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class Node extends BlockPos
{
    public final PipeNetwork network;
    public final Collection<Link> linkedWith;
    private TileEntity tile;

    public Node(PipeNetwork n, int x, int y, int z)
    {
        super(x, y, z);
        network = n;
        linkedWith = new HashSet<>();
    }

    public Node(PipeNetwork n, Vec3i v)
    {
        this(n, v.getX(), v.getY(), v.getZ());
    }

    @Nullable
    public TileEntity getTile()
    {
        if(tile == null || tile.isInvalid())
        {
            tile = network.world.getTileEntity(this);
        }

        return tile;
    }

    public void clearCache()
    {
        tile = null;
    }
}