package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.tile.TileTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderTank extends TileEntitySpecialRenderer<TileTank>
{
	@Override
	public void render(TileTank tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		int amount = tile.tank.getFluidAmount();

		if (amount <= 0)
		{
			return;
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		double o0 = 1.01D / 16D;
		double o1 = 14.99D / 16D;

		double y0 = o0;
		double y11 = o1;
		boolean upEmpty = false;

		TileEntity other = tile.getWorld().getTileEntity(tile.getPos().down());

		if (other instanceof TileTank)
		{
			y0 = 0D;
		}

		other = tile.getWorld().getTileEntity(tile.getPos().up());

		if (other instanceof TileTank)
		{
			y11 = 1D;
			upEmpty = ((TileTank) other).tank.getFluidAmount() <= 0;
		}

		double y1 = y0 + ((y11 - y0) * amount / (double) tile.tank.getCapacity());

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0F, 1F, 0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(180F, 0F, 0F, 1F);
		GlStateManager.rotate(180F, 1F, 0F, 0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(tile.tank.getFluid().getFluid().getStill(tile.tank.getFluid()).toString());

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		double u0 = sprite.getMinU();
		double v0 = sprite.getMinV();
		double u1 = sprite.getMaxU();
		double v1 = sprite.getMaxV();
		double uo = (u1 - u0) / 16D;
		double vo = (v1 - v0) / 16D;

		int color = tile.tank.getFluid().getFluid().getColor(tile.tank.getFluid());
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;

		//DOWN
		if (y0 > 0D)
		{
			buffer.pos(o0, y0, o0).tex(u0 + uo, v0 + vo).color(r, g, b, a).endVertex();
			buffer.pos(o1, y0, o0).tex(u1 - uo, v0 + vo).color(r, g, b, a).endVertex();
			buffer.pos(o1, y0, o1).tex(u1 - uo, v1 - vo).color(r, g, b, a).endVertex();
			buffer.pos(o0, y0, o1).tex(u0 + uo, v1 - vo).color(r, g, b, a).endVertex();
		}

		//UP
		if (y1 < 1D || upEmpty)
		{
			buffer.pos(o0, y1, o0).tex(u0 + uo, v0 + vo).color(r, g, b, a).endVertex();
			buffer.pos(o0, y1, o1).tex(u1 - uo, v0 + vo).color(r, g, b, a).endVertex();
			buffer.pos(o1, y1, o1).tex(u1 - uo, v1 - vo).color(r, g, b, a).endVertex();
			buffer.pos(o1, y1, o0).tex(u0 + uo, v1 - vo).color(r, g, b, a).endVertex();
		}

		double us0 = u0 + uo;
		double vs0 = v0 + uo;
		double us1 = u1 - uo;
		double vs1 = v1 - uo;

		buffer.pos(o0, y1, o0).tex(us0, vs0).color(r, g, b, a).endVertex();//SOUTH
		buffer.pos(o1, y1, o0).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o1, y0, o0).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, y0, o0).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o1).tex(us0, vs0).color(r, g, b, a).endVertex();//NORTH
		buffer.pos(o0, y1, o1).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o0, y0, o1).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y0, o1).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o0).tex(us0, vs0).color(r, g, b, a).endVertex();//EAST
		buffer.pos(o1, y1, o1).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o1, y0, o1).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y0, o0).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, y1, o1).tex(us0, vs0).color(r, g, b, a).endVertex();//WEST
		buffer.pos(o0, y1, o0).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o0, y0, o0).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, y0, o1).tex(us0, vs1).color(r, g, b, a).endVertex();

		tessellator.draw();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}
}