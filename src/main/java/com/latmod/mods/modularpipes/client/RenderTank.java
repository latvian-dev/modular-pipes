package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.block.ModularPipesBlocks;
import com.latmod.mods.modularpipes.item.ItemBlockTank;
import com.latmod.mods.modularpipes.tile.TileTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderTank extends TileEntitySpecialRenderer<TileTank>
{
	private static final TileTank DUMMY = new TileTank();

	@Override
	public void render(TileTank tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		int amount = tile.tank.getFluidAmount();

		if (amount <= 0)
		{
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		double o0 = 1.01D / 16D;
		double o1 = 14.99D / 16D;
		double y1 = o0 + ((o1 - o0) * amount / (double) tile.tank.getCapacity());

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0F, 1F, 0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(180F, 0F, 0F, 1F);
		GlStateManager.rotate(180F, 1F, 0F, 0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(tile.tank.getFluid().getFluid().getStill(tile.tank.getFluid()).toString());

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();
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
		buffer.pos(o0, o0, o0).tex(u0 + uo, v0 + vo).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o0).tex(u1 - uo, v0 + vo).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o1).tex(u1 - uo, v1 - vo).color(r, g, b, a).endVertex();
		buffer.pos(o0, o0, o1).tex(u0 + uo, v1 - vo).color(r, g, b, a).endVertex();

		buffer.pos(o0, y1, o0).tex(u0 + uo, v0 + vo).color(r, g, b, a).endVertex();
		buffer.pos(o0, y1, o1).tex(u1 - uo, v0 + vo).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o1).tex(u1 - uo, v1 - vo).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o0).tex(u0 + uo, v1 - vo).color(r, g, b, a).endVertex();

		double us0 = u0 + uo;
		double vs0 = v0 + uo;
		double us1 = u1 - uo;
		double vs1 = v1 - uo;

		buffer.pos(o0, y1, o0).tex(us0, vs0).color(r, g, b, a).endVertex();//SOUTH
		buffer.pos(o1, y1, o0).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o0).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, o0, o0).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o1).tex(us0, vs0).color(r, g, b, a).endVertex();//NORTH
		buffer.pos(o0, y1, o1).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o0, o0, o1).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o1).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, y1, o0).tex(us0, vs0).color(r, g, b, a).endVertex();//EAST
		buffer.pos(o1, y1, o1).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o1).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o1, o0, o0).tex(us0, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, y1, o1).tex(us0, vs0).color(r, g, b, a).endVertex();//WEST
		buffer.pos(o0, y1, o0).tex(us1, vs0).color(r, g, b, a).endVertex();
		buffer.pos(o0, o0, o0).tex(us1, vs1).color(r, g, b, a).endVertex();
		buffer.pos(o0, o0, o1).tex(us0, vs1).color(r, g, b, a).endVertex();

		tessellator.draw();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	public static class TankTEISR extends TileEntityItemStackRenderer
	{
		@Override
		public void renderByItem(ItemStack stack, float partialTicks)
		{
			Minecraft mc = Minecraft.getMinecraft();

			if (mc.world != null && mc.player != null)
			{
				try
				{
					//RenderHelper.enableStandardItemLighting();
					GlStateManager.disableLighting();
					GlStateManager.color(1F, 1F, 1F, 1F);
					GlStateManager.enableRescaleNormal();
					float lastBrightnessX = OpenGlHelper.lastBrightnessX;
					float lastBrightnessY = OpenGlHelper.lastBrightnessY;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
					GlStateManager.color(1F, 1F, 1F, 1F);
					IBlockState state = ModularPipesBlocks.TANK.getDefaultState();
					BlockPos pos = new BlockPos(mc.player.posX, 255, mc.player.posZ);
					GlStateManager.pushMatrix();
					GlStateManager.translate(-pos.getX(), -pos.getY(), -pos.getZ());
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.getBuffer();
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
					BlockRendererDispatcher rendererDispatcher = mc.getBlockRendererDispatcher();
					rendererDispatcher.getBlockModelRenderer().renderModelSmooth(mc.world, rendererDispatcher.getModelForState(state), state, pos, buffer, false, 0L);
					tessellator.draw();
					GlStateManager.popMatrix();
					GlStateManager.enableLighting();
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
				}
				catch (Exception ex)
				{
				}
			}

			IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			if (handler instanceof ItemBlockTank.TankCapProvider)
			{
				DUMMY.tank.setFluid(((ItemBlockTank.TankCapProvider) handler).tank.getFluid());
			}

			TileEntityRendererDispatcher.instance.render(DUMMY, 0D, 0D, 0D, partialTicks);
		}
	}
}