package com.latmod.modularpipes.data;

import com.latmod.modularpipes.block.PipeConnection;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public interface IPipeConnection
{
	default int getConnections()
	{
		return 0;
	}

	default PipeConnection getPipeConnectionType(EnumFacing facing)
	{
		return ((getConnections() & (1 << facing.getIndex())) != 0) ? PipeConnection.PIPE : PipeConnection.NONE;
	}
}