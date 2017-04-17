package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.PipeNetwork;
import com.latmod.modularpipes.api.IPipeController;
import com.latmod.modularpipes.api.IPipeNetworkTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TilePipeNetBase extends TileEntity implements ITickable, IPipeNetworkTile
{
    private boolean isDirty = true;
    private IPipeController controller;
    public int dimension;

    public TilePipeNetBase(int dim)
    {
        dimension = dim;
    }

    public void markDirty()
    {
        isDirty = true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("Dim", dimension);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        dimension = nbt.getInteger("Dim");
    }

    @Override
    public void onLoad()
    {
        PipeNetwork.addToNetwork(this, dimension);
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void update()
    {
        if(isDirty)
        {
            if(world != null && !world.isRemote)
            {
                updateContainingBlockInfo();
                world.markChunkDirty(pos, this);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 255);
            }

            isDirty = false;
        }
    }

    @Override
    public boolean hasError()
    {
        return false;
    }

    @Override
    public void setPipeController(IPipeController c)
    {
        controller = c;
    }

    @Override
    public IPipeController getPipeController()
    {
        return controller;
    }
}