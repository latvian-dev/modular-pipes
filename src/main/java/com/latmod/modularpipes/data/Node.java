package com.latmod.modularpipes.data;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class Node extends BlockPos
{
    public final PipeNetwork network;
    public final Collection<Link> linkedWith;
    private TileEntity[] tiles;

    public Node(PipeNetwork n, int x, int y, int z)
    {
        super(x, y, z);
        network = n;
        linkedWith = new HashSet<>();
        tiles = new TileEntity[7];
    }

    public Node(PipeNetwork n, Vec3i v)
    {
        this(n, v.getX(), v.getY(), v.getZ());
    }

    @Nullable
    public TileEntity getTile(int facing)
    {
        int i = facing < 0 || facing >= 6 ? 6 : facing;
        TileEntity prevTile = tiles[i];
        if(tiles[i] == null || tiles[i].isInvalid())
        {
            if(i == 6)
            {
                tiles[6] = network.world.getTileEntity(this);
            }
            else
            {
                tiles[i] = network.world.getTileEntity(offset(EnumFacing.VALUES[i]));

                if(tiles[i] != null && !tiles[i].hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite()))
                {
                    tiles[i] = null;
                }
            }
        }
        if(tiles[i] != null && tiles[i].isInvalid())
        {
            tiles[i] = null;
        }
        if(prevTile != tiles[i])
        {
            network.networkUpdated = true;
        }

        return tiles[i];
    }

    public void clearCache()
    {
        Arrays.fill(tiles, null);
    }
}