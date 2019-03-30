package com.latmod.mods.modularpipes.gui;

import com.latmod.mods.modularpipes.ModularPipes;
import com.latmod.mods.modularpipes.gui.painter.ContainerPainter;
import com.latmod.mods.modularpipes.gui.painter.GuiPainter;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
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

	public static void open(int id, EntityPlayer player, int x, int y, int z)
	{
		player.openGui(ModularPipes.INSTANCE, id, player.world, x, y, z);
	}

	public static final int MODULAR_PIPE_MIN = 1;
	public static final int MODULAR_PIPE_MAX = 6;
	public static final int PAINTER = 7;

	@Nullable
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == PAINTER)
		{
			return new ContainerPainter(player, player.getHeldItem(EnumHand.MAIN_HAND));
		}

		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (id >= MODULAR_PIPE_MIN && id <= MODULAR_PIPE_MAX)
		{
			if (tileEntity instanceof TilePipeModularMK1)
			{
				return new ContainerPipeModular(player, (TilePipeModularMK1) tileEntity, id - MODULAR_PIPE_MIN);
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
		if (id == PAINTER)
		{
			return new GuiPainter(new ContainerPainter(player, player.getHeldItem(EnumHand.MAIN_HAND)));
		}

		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (id >= MODULAR_PIPE_MIN && id <= MODULAR_PIPE_MAX)
		{
			if (tileEntity instanceof TilePipeModularMK1)
			{
				return new GuiPipeModular(new ContainerPipeModular(player, (TilePipeModularMK1) tileEntity, id - MODULAR_PIPE_MIN));
			}
		}

		return null;
	}
}