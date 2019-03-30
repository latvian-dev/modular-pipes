package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.item.ItemPainter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

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

			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();

			IBlockState state = Block.getStateById(ItemPainter.getPaint(gui.container.stack));

			if (!(state.getBlock() instanceof BlockAir))
			{
				TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				drawTexturedModalRect(x, y, sprite, width, height);
			}

			if (hovered)
			{
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(x, y, x + width, y + height, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
			}

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
	}

	@Override
	public void playPressSound(SoundHandler soundHandler)
	{
	}
}