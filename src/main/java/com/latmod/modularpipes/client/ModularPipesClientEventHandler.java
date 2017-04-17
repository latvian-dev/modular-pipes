package com.latmod.modularpipes.client;

import com.latmod.modularpipes.api.TransportedItem;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemSkull;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesClientEventHandler
{
    private static final List<TransportedItem> ITEMS = new ArrayList<>();
    private static final TIntObjectHashMap<TransportedItem> ITEM_MAP = new TIntObjectHashMap<>();

    public static void clear()
    {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(ITEMS.isEmpty())
        {
            return;
        }

        for(TransportedItem transportedItem : ITEMS)
        {
            if(transportedItem.action == TransportedItem.Action.HIDE)
            {
                continue;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 70, 0);
            //GlStateManager.translate(TilePipeBase.TEMP_POS[0], TilePipeBase.TEMP_POS[1], TilePipeBase.TEMP_POS[2]);
            GlStateManager.disableLighting();

            GlStateManager.scale(0.5F, 0.5F, 0.5F);

            if(transportedItem.stack.getItem() instanceof ItemSkull || !mc.getRenderItem().shouldRenderItemIn3D(transportedItem.stack))
            {
                GlStateManager.rotate(180F, 0F, 1F, 0F);
            }

            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItem(transportedItem.stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void updateItems(List<TransportedItem> updated, TIntArrayList removed)
    {
        if(!updated.isEmpty())
        {
            for(TransportedItem item : updated)
            {
                TransportedItem item1 = ITEM_MAP.put(item.id, item);

                if(item1 != null)
                {
                    item1.copyFrom(item);
                }
                else
                {
                    ITEM_MAP.put(item.id, item);
                    ITEMS.add(item);
                }
            }
        }

        updated.clear();

        if(!removed.isEmpty())
        {
            Iterator<TransportedItem> iterator = ITEMS.iterator();

            while(iterator.hasNext())
            {
                TransportedItem item = iterator.next();

                if(ITEM_MAP.containsKey(item.id))
                {
                    ITEM_MAP.remove(item.id);
                    iterator.remove();
                }
            }
        }

        removed.clear();
    }
}