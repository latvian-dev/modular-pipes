package com.latmod.modularpipes.data;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class CachedBlock
{
	@Nullable
	public Node getNode()
	{
		return null;
	}

	@Nullable
	public Link getLink()
	{
		return null;
	}

	public static class NodeData extends CachedBlock
	{
		private final Node node;

		public NodeData(Node n)
		{
			node = n;
		}

		@Override
		public Node getNode()
		{
			return node;
		}
	}

	public static class LinkData extends CachedBlock
	{
		private final Link link;

		public LinkData(Link l)
		{
			link = l;
		}

		@Override
		public Node getNode()
		{
			return link.start;
		}

		@Override
		public Link getLink()
		{
			return link;
		}
	}
}