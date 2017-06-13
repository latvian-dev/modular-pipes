package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.latmod.modularpipes.block.BlockPipeBase;
import com.latmod.modularpipes.block.EnumTier;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderModularPipe extends TileEntitySpecialRenderer<TileModularPipe>
{
	public static final TextureAtlasSprite[] SPRITES = new TextureAtlasSprite[EnumTier.VALUES.length];

	@Override
	public void func_192841_a(TileModularPipe te, double x, double y, double z, float partialTicks, int destroyStage, float p_192841_10_)
	{
		if (te.isInvalid())
		{
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x, y, z);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.depthMask(true);
		setLightmapDisabled(true);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableCull();

		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sprite = SPRITES[te.tier.ordinal()];
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		double s0 = (BlockPipeBase.SIZE - 0.03D) / 16D;
		double s1 = 1D - s0;

		double minU = sprite.getInterpolatedU(BlockPipeBase.SIZE);
		double minV = sprite.getInterpolatedV(BlockPipeBase.SIZE);
		double maxU = sprite.getInterpolatedU(16D - BlockPipeBase.SIZE);
		double maxV = sprite.getInterpolatedV(16D - BlockPipeBase.SIZE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(s0, s0, s0).tex(minU, minV).endVertex();
		buffer.pos(s1, s0, s0).tex(maxU, minV).endVertex();
		buffer.pos(s1, s0, s1).tex(maxU, maxV).endVertex();
		buffer.pos(s0, s0, s1).tex(minU, maxV).endVertex();
		buffer.pos(s0, s1, s0).tex(minU, minV).endVertex();
		buffer.pos(s0, s1, s1).tex(minU, maxV).endVertex();
		buffer.pos(s1, s1, s1).tex(maxU, maxV).endVertex();
		buffer.pos(s1, s1, s0).tex(maxU, minV).endVertex();
		buffer.pos(s0, s0, s0).tex(maxU, maxV).endVertex();
		buffer.pos(s0, s1, s0).tex(maxU, minV).endVertex();
		buffer.pos(s1, s1, s0).tex(minU, minV).endVertex();
		buffer.pos(s1, s0, s0).tex(minU, maxV).endVertex();
		buffer.pos(s0, s0, s1).tex(minU, maxV).endVertex();
		buffer.pos(s1, s0, s1).tex(maxU, maxV).endVertex();
		buffer.pos(s1, s1, s1).tex(maxU, minV).endVertex();
		buffer.pos(s0, s1, s1).tex(minU, minV).endVertex();
		buffer.pos(s0, s0, s0).tex(minU, maxV).endVertex();
		buffer.pos(s0, s0, s1).tex(maxU, maxV).endVertex();
		buffer.pos(s0, s1, s1).tex(maxU, minV).endVertex();
		buffer.pos(s0, s1, s0).tex(minU, minV).endVertex();
		buffer.pos(s1, s0, s0).tex(maxU, maxV).endVertex();
		buffer.pos(s1, s1, s0).tex(maxU, minV).endVertex();
		buffer.pos(s1, s1, s1).tex(minU, minV).endVertex();
		buffer.pos(s1, s0, s1).tex(minU, maxV).endVertex();
		tessellator.draw();

		GlStateManager.enableLighting();
		setLightmapDisabled(false);

		for (int i = 0; i < 6; i++)
		{
			if (te.modules[i].hasModule() && !te.modules[i].getItemStack().isEmpty())
			{
				EnumFacing facing = EnumFacing.VALUES[i];
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5D + facing.getFrontOffsetX() * 0.46975D, 0.5D + facing.getFrontOffsetY() * 0.46975D, 0.5D + facing.getFrontOffsetZ() * 0.46975D);
				//GlStateManager.rotate(180F, 0F, 0F, 1F);
				GlStateManager.rotate(MathUtils.ROTATION_Y[i], 0F, 1F, 0F);
				GlStateManager.rotate(MathUtils.ROTATION_X[i] + 90F, 1F, 0F, 0F);
				GlStateManager.scale(0.5D, 0.5D, 1D);
				ClientPipeNetwork.RENDER_ITEM.renderItem(te.modules[i].getItemStack(), ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.popMatrix();
			}
		}


		GlStateManager.disableRescaleNormal();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}
}