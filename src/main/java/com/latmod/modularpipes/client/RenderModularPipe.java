package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.BlockPipeBase;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public class RenderModularPipe extends TileEntitySpecialRenderer<TileModularPipe>
{
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];

    private static final CachedVertexData TIER_MODEL = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    private static final CachedVertexData TIER_7_MODEL = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    private static final double TEX = BlockPipeBase.SIZE / 16D;
    private static final double SIZE = TEX - 0.03D / 16D;

    static
    {
        TIER_MODEL.minU = TIER_MODEL.minV = TIER_7_MODEL.minU = TIER_7_MODEL.minV = TEX;
        TIER_MODEL.maxU = TIER_MODEL.maxV = TIER_7_MODEL.maxU = TIER_7_MODEL.maxV = 1D - TEX;

        for(int i = 0; i < TEXTURES.length; i++)
        {
            TEXTURES[i] = new ResourceLocation(ModularPipes.MOD_ID, "textures/blocks/pipes/tier_" + i + ".png");
        }

        TIER_MODEL.cube(SIZE, SIZE, SIZE, 1D - SIZE, 1D - SIZE, 1D - SIZE);
        TIER_7_MODEL.color.set(Color4I.WHITE);
    }

    @Override
    public void renderTileEntityAt(TileModularPipe te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if(te.isInvalid())
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

        mc.getTextureManager().bindTexture(TEXTURES[te.tier & 7]);
        Tessellator tessellator = Tessellator.getInstance();

        if(te.tier >= 7)
        {
            TIER_7_MODEL.reset();
            TIER_7_MODEL.color.setFromHSB((float) ((Minecraft.getSystemTime() * 0.0003D) % 1D), 1F, 1F);
            TIER_7_MODEL.cube(SIZE, SIZE, SIZE, 1D - SIZE, 1D - SIZE, 1D - SIZE);
            TIER_7_MODEL.draw(tessellator, tessellator.getBuffer());
        }
        else
        {
            TIER_MODEL.draw(tessellator, tessellator.getBuffer());
        }

        GlStateManager.enableLighting();
        setLightmapDisabled(false);

        for(int i = 0; i < 6; i++)
        {
            if(te.modules[i].hasModule() && !te.modules[i].getItemStack().isEmpty())
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