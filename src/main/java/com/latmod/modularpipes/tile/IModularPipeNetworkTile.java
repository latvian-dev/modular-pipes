package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.data.IPipeConnection;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface IModularPipeNetworkTile extends IPipeConnection
{
	void updateNetworkTile();

	@Nullable
	TileController getController();

	void setControllerPosition(BlockPos pos);

	default boolean hasError()
	{
		TileController controller = getController();
		return controller == null || controller.hasError();
	}
}