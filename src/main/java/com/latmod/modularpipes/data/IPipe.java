package com.latmod.modularpipes.data;

import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public interface IPipe extends IPipeConnection
{
	default boolean isPipeOpaque()
	{
		return false;
	}

	default NodeType getNodeType()
	{
		return NodeType.NONE;
	}

	default double getItemSpeedModifier(TransportedItem item)
	{
		return 1D;
	}

	default EnumFacing getPipeFacing(EnumFacing source)
	{
		return source;
	}

	default EnumFacing getItemDirection(TransportedItem item, EnumFacing source)
	{
		return getPipeFacing(source);
	}

	default void onItemEntered(TransportedItem item, EnumFacing facing)
	{
	}

	default void onItemExited(TransportedItem item, EnumFacing facing)
	{
	}
}