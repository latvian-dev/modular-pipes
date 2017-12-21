package com.latmod.modularpipes.tile;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface IModularPipeNetworkTile
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