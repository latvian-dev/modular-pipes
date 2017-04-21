package com.latmod.modularpipes.api_impl;

import com.latmod.modularpipes.api.ILink;
import com.latmod.modularpipes.api.INode;
import com.latmod.modularpipes.api.IPipeNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class Node implements INode
{
    private final IPipeNetwork network;
    private final BlockPos pos;
    private final Collection<ILink> linkedWith;
    private TileEntity tile;

    public Node(IPipeNetwork n, BlockPos p)
    {
        network = n;
        pos = p;
        linkedWith = new HashSet<>();
    }

    @Override
    public IPipeNetwork getNetwork()
    {
        return network;
    }

    @Override
    public BlockPos getPos()
    {
        return pos;
    }

    @Override
    @Nullable
    public TileEntity getTile()
    {
        if(tile == null)
        {
            World w = getNetwork().getWorld();

            if(w != null)
            {
                tile = w.getTileEntity(getPos());
            }
        }

        return tile;
    }

    @Override
    public Collection<ILink> getLinkedWith()
    {
        return linkedWith;
    }

    @Override
    public void clearCache()
    {
        tile = null;
    }
}