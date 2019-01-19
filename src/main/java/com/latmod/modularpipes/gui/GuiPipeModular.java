package com.latmod.modularpipes.gui;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class GuiPipeModular extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModularPipes.MOD_ID, "textures/gui/pipe_modular.png");

	public final ContainerPipeModular container;

	public GuiPipeModular(ContainerPipeModular c)
	{
		super(c);
		ySize = 118;
		container = c;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}