package com.latmod.modularpipes.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.latmod.modularpipes.data.PipeNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class MessageVisualizeNetwork extends MessageToClient
{
	private static final DataOut.Serializer<Collection<BlockPos>> BLOCK_POS_LIST_SERIALIZER = (data, object) -> data.writeCollection(object, DataOut.BLOCK_POS);
	private static final DataIn.Deserializer<Collection<BlockPos>> BLOCK_POS_LIST_DESERIALIZER = data -> data.readCollection(null, DataIn.BLOCK_POS);

	private Collection<BlockPos> nodesSimple;
	private Collection<BlockPos> nodesTiles;
	private Collection<Collection<BlockPos>> links;
	private Collection<BlockPos> tiles;

	public MessageVisualizeNetwork()
	{
	}

	public MessageVisualizeNetwork(Collection<BlockPos> ns, Collection<BlockPos> nt, Collection<Collection<BlockPos>> l, Collection<BlockPos> t)
	{
		nodesSimple = ns;
		nodesTiles = nt;
		links = l;
		tiles = t;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return ModularPipesNet.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeCollection(nodesSimple, DataOut.BLOCK_POS);
		data.writeCollection(nodesTiles, DataOut.BLOCK_POS);
		data.writeCollection(links, BLOCK_POS_LIST_SERIALIZER);
		data.writeCollection(tiles, DataOut.BLOCK_POS);
	}

	@Override
	public void readData(DataIn data)
	{
		nodesSimple = data.readCollection(DataIn.BLOCK_POS);
		nodesTiles = data.readCollection(DataIn.BLOCK_POS);
		links = data.readCollection(BLOCK_POS_LIST_DESERIALIZER);
		tiles = data.readCollection(DataIn.BLOCK_POS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		PipeNetwork.get(ClientUtils.MC.world).visualizeNetwork(nodesSimple, nodesTiles, links, tiles);
	}
}