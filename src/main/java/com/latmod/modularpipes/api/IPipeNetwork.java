package com.latmod.modularpipes.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public interface IPipeNetwork
{
    int getDimension();

    @Nullable
    World getWorld();

    @Nullable
    INode getNode(BlockPos pos);

    void setNode(BlockPos pos, @Nullable INode node);

    Collection<INode> getNodes();

    List<ILink> getPathList(BlockPos pos, boolean useTempList);

    @Nullable
    ILink getBestPath(BlockPos from, BlockPos to);

    void addOrUpdatePipe(BlockPos pos, IBlockState state);

    void removeLinkAt(BlockPos pos, IBlockState state);

    void addItem(TransportedItem item);

    boolean generatePath(ModuleContainer container, TransportedItem item);
}