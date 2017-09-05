package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.util.InvUtils;
import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
		if (world.isRemote)
		{
			return ModularPipes.PROXY.getClientNetwork(world);
		}

		int dim = world.provider.getDimension();
		ServerPipeNetwork net = ServerPipeNetwork.NETWORK_MAP.get(dim);

		if (net == null)
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
	public boolean networkUpdated;

	private final Consumer<TransportedItem> foreachUpdate = item ->
	{
		item.update();

		if (item.remove())
		{
			itemsRemoved = true;
		}

		if (item.action.update())
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

	public Collection<Link> getLinks()
	{
		return Collections.emptyList();
	}

	public boolean removePipe(BlockPos pos, boolean simulate)
	{
		return false;
	}

	public void addPipe(BlockPos pos, IBlockState state)
	{
	}

	public void addItem(TransportedItem item)
	{
		item.action = TransportedItem.Action.UPDATE;
		items.put(item.id, item);
	}

	@Override
	public void update()
	{
		if (items.isEmpty())
		{
			return;
		}

		itemsRemoved = false;
		items.values().forEach(foreachUpdate);

		if (itemsRemoved)
		{
			Iterator<TransportedItem> iterator = items.values().iterator();
			while (iterator.hasNext())
			{
				TransportedItem item = iterator.next();

				if (item.remove())
				{
					if (!world.isRemote && item.action == TransportedItem.Action.DROP)
					{
						InvUtils.dropItem(world, item.pos, item.stack, 12);
					}

					iterator.remove();
				}
			}
		}

		for (TransportedItem item : items.values())
		{
			item.postUpdate();
		}
	}

	public void itemUpdated(int id, TransportedItem item)
	{
	}

	public void visualizeNetwork(Map<BlockPos, NodeType> nodes, Collection<List<BlockPos>> links, Collection<BlockPos> tiles)
	{
	}
}