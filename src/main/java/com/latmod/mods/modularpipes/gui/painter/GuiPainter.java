package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.ModularPipes;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * @author LatvianModder
 */
public class GuiPainter extends ContainerScreen<ContainerPainter>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModularPipes.MOD_ID, "textures/gui/painter.png");

	public final ContainerPainter container;
	public ButtonPaint buttonPaint;

	public GuiPainter(ContainerPainter c, PlayerInventory inv, ITextComponent comp)
	{
		super(c, inv, comp);
		container = c;
		ySize = 118;
	}

	@Override
	public void init()
	{
		super.init();
		addButton(buttonPaint = new ButtonPaint(this, guiLeft + 80, guiTop + 11, p_onPress_1_ -> {
			if (container.enchantItem(container.player, 0))
			{
				minecraft.playerController.sendEnchantPacket(container.windowId, 0);
			}
		}));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		GuiUtils.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize, 0);
	}


}
