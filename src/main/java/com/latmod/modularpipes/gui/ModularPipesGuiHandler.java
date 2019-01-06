package com.latmod.modularpipes.gui;

import com.latmod.modularpipes.tile.TilePipeDiamond;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public enum ModularPipesGuiHandler implements IGuiHandler
{
	INSTANCE;

	public static final int DIAMOND_PIPE = 1;

	@Nullable
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == DIAMOND_PIPE)
		{
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

			if (tileEntity instanceof TilePipeDiamond)
			{
				return new ContainerDiamondPipe(player, (TilePipeDiamond) tileEntity);
			}
		}

		return null;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		return getClientGuiElement0(id, player, world, x, y, z);
	}

	@Nullable
	private Object getClientGuiElement0(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == DIAMOND_PIPE)
		{
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

			if (tileEntity instanceof TilePipeDiamond)
			{
				return new GuiDiamondPipe(new ContainerDiamondPipe(player, (TilePipeDiamond) tileEntity));
			}
		}

		return null;
	}
}