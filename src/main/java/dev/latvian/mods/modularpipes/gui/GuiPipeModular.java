package dev.latvian.mods.modularpipes.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * @author LatvianModder
 */
public class GuiPipeModular extends ContainerScreen<ContainerPipeModular> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModularPipes.MOD_ID, "textures/gui/pipe_modular.png");

	public final ContainerPipeModular container;

	public GuiPipeModular(ContainerPipeModular c, PlayerInventory inv, ITextComponent comp) {
		super(c, inv, comp);
		ySize = 118;
		container = c;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		GuiUtils.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize, 0);
	}
}