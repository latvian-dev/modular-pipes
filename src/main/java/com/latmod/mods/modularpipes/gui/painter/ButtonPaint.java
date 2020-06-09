package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.item.ItemPainter;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * @author LatvianModder
 */
public class ButtonPaint extends Button
{
	public final GuiPainter gui;

	public ButtonPaint(GuiPainter g, int x, int y, IPressable pressable)
	{
		super(x, y, 16, 16, "", pressable);
		gui = g;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();

			BlockState state = Block.getStateById(ItemPainter.getPaint(gui.container.stack));

			if (state.getBlock()!= Blocks.AIR)
			{
//				TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
//				Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//				GuiUtils.drawTexturedModalRect(x, y, sprite.getMinU(), width, height);
			}

			if (isHovered())
			{
				GlStateManager.colorMask(true, true, true, false);
				GuiUtils.drawGradientRect(0, x, y, x + width, y + height, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
			}

			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
		}
	}
}