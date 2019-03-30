package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.item.ItemPainter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ButtonPaint extends GuiButton
{
	public final GuiPainter gui;

	public ButtonPaint(GuiPainter g, int id, int x, int y)
	{
		super(id, x, y, 16, 16, "");
		gui = g;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			ItemStack paint = ItemPainter.getPaint(gui.container.stack);

			if (!paint.isEmpty())
			{
				GlStateManager.color(1F, 1F, 1F, 1F);
				GlStateManager.enableDepth();
				RenderHelper.enableGUIStandardItemLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				RenderItem renderItem = mc.getRenderItem();
				renderItem.renderItemAndEffectIntoGUI(mc.player, paint, x, y);
				renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, paint, x, y, null);
				RenderHelper.disableStandardItemLighting();
			}

			if (hovered)
			{
				GlStateManager.color(1F, 1F, 1F, 1F);
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(x, y, x + width, y + height, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
	}

	@Override
	public void playPressSound(SoundHandler soundHandler)
	{
	}
}