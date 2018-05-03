package com.latmod.modularpipes.tile;

import com.feed_the_beast.ftblib.lib.io.Bits;
import com.latmod.modularpipes.data.NodeType;

/**
 * @author LatvianModder
 */
public class TileBasicPipe extends TilePipeBase
{
	@Override
	public NodeType getNodeType()
	{
		int sum = 0;

		for (int facing = 0; facing < 6; facing++)
		{
			if (Bits.getFlag(getConnections(), facing))
			{
				sum++;

				if (sum >= 3)
				{
					return NodeType.SIMPLE;
				}
			}
		}

		return NodeType.NONE;
	}
}