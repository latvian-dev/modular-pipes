package com.latmod.modularpipes.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.latmod.modularpipes.tile.TileController;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderController extends TileEntitySpecialRenderer<TileController>
{
	public static TextureAtlasSprite CONTROLLER_NO_ERROR, CONTROLLER_ERROR, PIPE_ERROR;

	@Override
	public void render(TileController controller, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if (controller.isInvalid())
		{
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x, y, z);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.depthMask(true);
		setLightmapDisabled(true);
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		TextureAtlasSprite sprite;

		if (controller.hasError())
		{
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			sprite = CONTROLLER_ERROR;
		}
		else
		{
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			sprite = CONTROLLER_NO_ERROR;
		}

		ClientUtils.MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		double s0 = -0.03D / 16D;
		double s1 = 1D - s0;

		double minU = sprite.getMinU();
		double minV = sprite.getMinV();
		double maxU = sprite.getMaxU();
		double maxV = sprite.getMaxV();
		int alphai = 255;

		if (destroyStage < 0)
		{
			alphai = (int) (alpha * 255);
		}

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		buffer.pos(s0, s0, s0).tex(minU, minV).color(255, 255, 255, alphai).normal(0, -1, 0).endVertex();
		buffer.pos(s1, s0, s0).tex(maxU, minV).color(255, 255, 255, alphai).normal(0, -1, 0).endVertex();
		buffer.pos(s1, s0, s1).tex(maxU, maxV).color(255, 255, 255, alphai).normal(0, -1, 0).endVertex();
		buffer.pos(s0, s0, s1).tex(minU, maxV).color(255, 255, 255, alphai).normal(0, -1, 0).endVertex();
		buffer.pos(s0, s1, s0).tex(minU, minV).color(255, 255, 255, alphai).normal(0, 1, 0).endVertex();
		buffer.pos(s0, s1, s1).tex(minU, maxV).color(255, 255, 255, alphai).normal(0, 1, 0).endVertex();
		buffer.pos(s1, s1, s1).tex(maxU, maxV).color(255, 255, 255, alphai).normal(0, 1, 0).endVertex();
		buffer.pos(s1, s1, s0).tex(maxU, minV).color(255, 255, 255, alphai).normal(0, 1, 0).endVertex();
		buffer.pos(s0, s0, s0).tex(maxU, maxV).color(255, 255, 255, alphai).normal(0, 0, -1).endVertex();
		buffer.pos(s0, s1, s0).tex(maxU, minV).color(255, 255, 255, alphai).normal(0, 0, -1).endVertex();
		buffer.pos(s1, s1, s0).tex(minU, minV).color(255, 255, 255, alphai).normal(0, 0, -1).endVertex();
		buffer.pos(s1, s0, s0).tex(minU, maxV).color(255, 255, 255, alphai).normal(0, 0, -1).endVertex();
		buffer.pos(s0, s0, s1).tex(minU, maxV).color(255, 255, 255, alphai).normal(0, 0, 1).endVertex();
		buffer.pos(s1, s0, s1).tex(maxU, maxV).color(255, 255, 255, alphai).normal(0, 0, 1).endVertex();
		buffer.pos(s1, s1, s1).tex(maxU, minV).color(255, 255, 255, alphai).normal(0, 0, 1).endVertex();
		buffer.pos(s0, s1, s1).tex(minU, minV).color(255, 255, 255, alphai).normal(0, 0, 1).endVertex();
		buffer.pos(s0, s0, s0).tex(minU, maxV).color(255, 255, 255, alphai).normal(-1, 0, 0).endVertex();
		buffer.pos(s0, s0, s1).tex(maxU, maxV).color(255, 255, 255, alphai).normal(-1, 0, 0).endVertex();
		buffer.pos(s0, s1, s1).tex(maxU, minV).color(255, 255, 255, alphai).normal(-1, 0, 0).endVertex();
		buffer.pos(s0, s1, s0).tex(minU, minV).color(255, 255, 255, alphai).normal(-1, 0, 0).endVertex();
		buffer.pos(s1, s0, s0).tex(maxU, maxV).color(255, 255, 255, alphai).normal(1, 0, 0).endVertex();
		buffer.pos(s1, s1, s0).tex(maxU, minV).color(255, 255, 255, alphai).normal(1, 0, 0).endVertex();
		buffer.pos(s1, s1, s1).tex(minU, minV).color(255, 255, 255, alphai).normal(1, 0, 0).endVertex();
		buffer.pos(s1, s0, s1).tex(minU, maxV).color(255, 255, 255, alphai).normal(1, 0, 0).endVertex();
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableLighting();
		setLightmapDisabled(false);
		GlStateManager.disableRescaleNormal();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}
}