package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.world.World;

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
        if(items.isEmpty())
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
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}