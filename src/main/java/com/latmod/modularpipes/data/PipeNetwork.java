package com.latmod.modularpipes.data;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public abstract class PipeNetwork implements ITickable
{
    public static PipeNetwork get(World world)
    {
        if(world.isRemote)
        {
            return ModularPipes.PROXY.getClientNetwork();
        }

        int dim = world.provider.getDimension();
        PipeNetwork net = ServerPipeNetwork.NETWORK_MAP.get(dim);

        if(net == null)
        {
            net = new ServerPipeNetwork(world);
            ServerPipeNetwork.NETWORK_MAP.put(dim, net);
        }

        return net;
    }

    // End of static //

    public final World world;
    public final Map<Integer, TransportedItem> items = new HashMap<>();
    private boolean itemsRemoved = false;

    private final Consumer<TransportedItem> foreachUpdate = item ->
    {
        item.update();

        if(item.remove())
        {
            itemsRemoved = true;
        }

        if(item.action.update())
        {
            itemUpdated(item.id, item);
        }
    };

    public PipeNetwork(World w)
    {
        world = w;
    }

    public ServerPipeNetwork server()
    {
        throw new IllegalStateException();
    }

    public void clear()
    {
        items.clear();
    }

    @Nullable
    public Node getNode(BlockPos pos)
    {
        return null;
    }

    public Collection<Node> getNodes()
    {
        return Collections.emptyList();
    }

    public List<Link> getPathList(BlockPos pos, boolean useTempList)
    {
        return Collections.emptyList();
    }

    @Nullable
    public Link getBestPath(Node from, Node to)
    {
        return null;
    }

    public void addOrUpdatePipe(BlockPos pos, IBlockState state, IPipeBlock block)
    {
    }

    public void removePipe(BlockPos pos, IBlockState state, IPipeBlock block)
    {
    }

    public void addItem(TransportedItem item)
    {
        item.action = TransportedItem.Action.UPDATE;
        items.put(item.id, item);
    }

    public boolean generatePath(ModuleContainer container, TransportedItem item)
    {
        return false;
    }

    @Override
    public void update()
    {
        if(items.isEmpty())
        {
            return;
        }

        itemsRemoved = false;
        items.values().forEach(foreachUpdate);

        if(itemsRemoved)
        {
            items.values().removeIf(TransportedItem.REMOVE_PREDICATE);
        }

        items.values().forEach(TransportedItem.FOREACH_POST_UPDATE);
    }

    public void itemUpdated(int id, TransportedItem item)
    {
    }
}