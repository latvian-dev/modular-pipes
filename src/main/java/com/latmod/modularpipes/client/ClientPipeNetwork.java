package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import com.latmod.modularpipes.item.ItemDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ClientPipeNetwork extends PipeNetwork
{
    private static ClientPipeNetwork INSTANCE;
    public static RenderItem RENDER_ITEM = Minecraft.getMinecraft().getRenderItem();

    public static ClientPipeNetwork get()
    {
        World world = Minecraft.getMinecraft().world;

        if(world == null)
        {
            throw new IllegalStateException();
        }
        else if(INSTANCE == null || world != INSTANCE.world)
        {
            INSTANCE = new ClientPipeNetwork(world);
        }
        return INSTANCE;
    }

    private static final Function<Integer, ClientTransportedItem> COMPUTE_ABSENT = ClientTransportedItem::new;

    public final BiConsumer<? super Integer, ? super TransportedItem> foreachUpdateItems = (id, item) ->
    {
        if(item == null || item.remove())
        {
            items.remove(id);
        }
        else
        {
            items.computeIfAbsent(id, COMPUTE_ABSENT).copyFrom(item);
        }
    };

    private CachedVertexData networkVis;

    private ClientPipeNetwork(World w)
    {
        super(w);
    }

    @Override
    public void clear()
    {
        super.clear();
        INSTANCE = null;
    }

    public void render(float pt)
    {
        if(items.isEmpty() && networkVis == null)
        {
            return;
        }

        double renderDistanceSq = 80 * 80;
        double x, y, z, s2;
        double px = FTBLibClient.playerX;
        double py = FTBLibClient.playerY;
        double pz = FTBLibClient.playerZ;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-px, -py, -pz);
        GlStateManager.disableLighting();
        RenderHelper.enableStandardItemLighting();

        for(TransportedItem item : items.values())
        {
            ClientTransportedItem i = item.client();

            if(i.visible())
            {
                x = i.prevX + (i.posX - i.prevX) * pt;
                y = i.prevY + (i.posY - i.prevY) * pt;
                z = i.prevZ + (i.posZ - i.prevZ) * pt;

                if(MathUtils.distSq(x, y, z, px, py, pz) <= renderDistanceSq)
                {
                    s2 = i.scale / 2D;
                    if(FTBLibClient.FRUSTUM.isBoxInFrustum(x - s2, y - s2, z - s2, x + s2, y + s2, z + s2))
                    {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x, y, z);
                        GlStateManager.scale(i.scale, i.scale, i.scale);
                        GlStateManager.rotate(i.rotationY, 0F, 1F, 0F);
                        ClientPipeNetwork.RENDER_ITEM.renderItem(i.stack, ItemCameraTransforms.TransformType.FIXED);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }

        RenderHelper.disableStandardItemLighting();

        if(networkVis != null && Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemDebug)
        {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.color(1F, 1F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            networkVis.draw(tessellator, buffer);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public void visualizeNetwork(Collection<BlockPos> nodes, Collection<List<BlockPos>> links, Collection<BlockPos> tiles)
    {
        if(nodes.isEmpty() && links.isEmpty() && tiles.isEmpty())
        {
            networkVis = null;
            return;
        }

        networkVis = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        networkVis.color.set(0x66FF0000);

        for(BlockPos pos : nodes)
        {
            networkVis.centeredCube(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.3D);
        }

        for(List<BlockPos> l : links)
        {
            for(int i = 0; i < l.size(); i++)
            {
                BlockPos pos = l.get(i);
                networkVis.color.set(0x6600FF00);
                networkVis.centeredCube(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.1D);

                if(i == 0 || i < l.size() - 1)
                {
                    BlockPos next = l.get(i + 1);

                    Vec3d center = MathUtils.getMidPoint(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, next.getX() + 0.5D, next.getY() + 0.5D, next.getZ() + 0.5D, 0.5D);

                    networkVis.color.set(0x660000FF);
                    BlockPos size = pos.subtract(next);
                    networkVis.centeredCube(center.xCoord, center.yCoord, center.zCoord, conSize(size.getX()), conSize(size.getY()), conSize(size.getZ()));
                }
            }
        }

        networkVis.color.set(0x66FFFF00);

        for(BlockPos pos : tiles)
        {
            networkVis.cube(world.getBlockState(pos).getBoundingBox(world, pos).expand(0.01D, 0.01D, 0.01D).offset(pos));
        }
    }

    private static double conSize(int s)
    {
        return s != 0 ? (Math.abs(s) / 2D - 0.1D) : 0.05D;
    }
}