package com.latmod.modularpipes.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.latmod.modularpipes.client.ClientTransportedItem;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class MessageUpdateItems extends MessageToClient
{
	private Collection<TransportedItem> updated;

	public MessageUpdateItems()
	{
	}

	public MessageUpdateItems(Collection<TransportedItem> u)
	{
		updated = u;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return ModularPipesNet.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeCollection(updated, TransportedItem.SERIALIZER);
	}

	@Override
	public void readData(DataIn data)
	{
		updated = data.readCollection(TransportedItem.DESERIALIZER);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		PipeNetwork n = PipeNetwork.get(ClientUtils.MC.world);

		for (TransportedItem item : updated)
		{
			if (item.remove())
			{
				n.items.remove(item.id);
			}
			else
			{
				TransportedItem citem = n.items.get(item.id);

				if (citem == null)
				{
					citem = new ClientTransportedItem(n, item.id);
					n.items.put(item.id, citem);
				}

				citem.copyFrom(item);
			}
		}
	}
}