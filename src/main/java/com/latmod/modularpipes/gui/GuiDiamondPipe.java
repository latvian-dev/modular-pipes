package com.latmod.modularpipes.gui;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author LatvianModder
 */
public class GuiDiamondPipe extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModularPipes.MOD_ID, "textures/gui/pipe_diamond.png");

	private class ButtonFilter extends GuiButton
	{
		public ButtonFilter(int id, int x, int y)
		{
			super(id, x, y, 16, 16, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
		{
			if (visible)
			{
				GlStateManager.color(1F, 1F, 1F, 1F);
				hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

				if (hovered)
				{
					//int left, int top, int right, int bottom
					drawRect(x, y, x + width, y + height, -2130706433);
				}

				if (!container.pipe.inventories[id].filter.isEmpty())
				{
					zLevel = 100F;
					itemRender.zLevel = 100F;
					GlStateManager.enableDepth();
					RenderHelper.enableGUIStandardItemLighting();
					itemRender.renderItemAndEffectIntoGUI(mc.player, container.pipe.inventories[id].filter, x, y);
					itemRender.renderItemOverlayIntoGUI(fontRenderer, container.pipe.inventories[id].filter, x, y, "");
					itemRender.zLevel = 0F;
					zLevel = 0F;
				}

				mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public final ContainerDiamondPipe container;

	public GuiDiamondPipe(ContainerDiamondPipe c)
	{
		super(c);
		ySize = 118;
		container = c;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		for (int i = 0; i < 6; i++)
		{
			buttonList.add(new ButtonFilter(i, guiLeft + 20 + i * 24, guiTop + 11));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button instanceof ButtonFilter)
		{
			if (container.enchantItem(container.player, button.id))
			{
				mc.playerController.sendEnchantPacket(container.windowId, button.id);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void renderHoveredToolTip(int x, int y)
	{
		super.renderHoveredToolTip(x, y);

		for (GuiButton button : buttonList)
		{
			if (button.isMouseOver() && button instanceof ButtonFilter)
			{
				ItemStack stack = container.pipe.inventories[button.id].filter;

				if (!stack.isEmpty())
				{
					renderToolTip(stack, x, y);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}