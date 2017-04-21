package com.latmod.modularpipes.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public interface INode
{
    IPipeNetwork getNetwork();

    BlockPos getPos();

    @Nullable
    TileEntity getTile();

    Collection<ILink> getLinkedWith();

    void clearCache();
}