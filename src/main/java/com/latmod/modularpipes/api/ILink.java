package com.latmod.modularpipes.api;

import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public interface ILink
{
    Comparator<ILink> COMPARATOR = Comparator.comparing(ILink::getLength);

    IPipeNetwork getNetwork();

    UUID getId();

    INode getStart();

    INode getEnd();

    List<BlockPos> getPath();

    int getActualLength();

    float getLength();

    void simplify();

    boolean contains(BlockPos pos);
}