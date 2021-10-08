package dev.latvian.mods.modularpipes.gui;

import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public enum ModularPipesGuiHandler {
	INSTANCE;

	public static void open(int id, PlayerEntity player, int x, int y, int z) {
//		ScreenManager.openScreen(ModularPipesContainers.PIPE_MODULAR, Minecraft.getInstance(), 0, new StringTextComponent(""));
//		player.openGui(ModularPipes.INSTANCE, id, player.world, x, y, z);
	}

	public static final int MODULAR_PIPE_MIN = 1;
	public static final int MODULAR_PIPE_MAX = 6;
	public static final int PAINTER = 7;
//
//	@Nullable
//	public Object getServerGuiElement(int id, PlayerEntity player, World world, int x, int y, int z)
//	{
//		if (id == PAINTER)
//		{
//			return new ContainerPainter(player, player.getHeldItem(Hand.MAIN_HAND));
//		}
//
//		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
//
//		if (id >= MODULAR_PIPE_MIN && id <= MODULAR_PIPE_MAX)
//		{
//			if (tileEntity instanceof TilePipeModularMK1)
//			{
//				return new ContainerPipeModular(player, (TilePipeModularMK1) tileEntity, id - MODULAR_PIPE_MIN);
//			}
//		}
//
//		return null;
//	}
//
//	@Nullable
//	public Object getClientGuiElement(int id, PlayerEntity player, World world, int x, int y, int z)
//	{
//		return getClientGuiElement0(id, player, world, x, y, z);
//	}
//
//	@Nullable
//	private Object getClientGuiElement0(int id, PlayerEntity player, World world, int x, int y, int z)
//	{
//		if (id == PAINTER)
//		{
//			return new GuiPainter(new ContainerPainter(player, player.getHeldItem(Hand.MAIN_HAND)));
//		}
//
//		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
//
//		if (id >= MODULAR_PIPE_MIN && id <= MODULAR_PIPE_MAX)
//		{
//			if (tileEntity instanceof TilePipeModularMK1)
//			{
//				return new GuiPipeModular(new ContainerPipeModular(player, (TilePipeModularMK1) tileEntity, id - MODULAR_PIPE_MIN));
//			}
//		}
//
//		return null;
//	}
}