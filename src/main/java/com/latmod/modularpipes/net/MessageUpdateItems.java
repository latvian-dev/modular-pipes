package com.latmod.modularpipes.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.latmod.modularpipes.client.ClientTransportedItem;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class MessageUpdateItems extends MessageToClient<MessageUpdateItems>
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
	public void onMessage(MessageUpdateItems message, EntityPlayer player)
	{
		PipeNetwork n = PipeNetwork.get(player.world);

		for (TransportedItem item : message.updated)
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