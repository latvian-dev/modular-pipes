package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.data.TransportedItem;
import com.latmod.modularpipes.data.TransportedItemClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ModularPipesClientEventHandler
{
    private static final Map<Integer, TransportedItemClient> ITEMS = new HashMap<>();
    private static final Consumer<TransportedItemClient> FOREACH_UPDATE = TransportedItemClient::update;
    private static final Consumer<TransportedItemClient> FOREACH_RENDER = TransportedItemClient::render;
    private static final Function<Integer, TransportedItemClient> COMPUTE_ABSENT = k -> new TransportedItemClient();

    public static final BiConsumer<? super Integer, ? super TransportedItem> FOREACH_UPDATE_ITEMS = (id, item) ->
    {
        if(item == null || item.action == TransportedItem.Action.REMOVE)
        {
            ITEMS.remove(id);
        }
        else
        {
            ITEMS.computeIfAbsent(id, COMPUTE_ABSENT).copyFrom(item);
        }
    };

    public static float partialTicks;
    public static RenderItem RENDER_ITEM = Minecraft.getMinecraft().getRenderItem();

    public static void clear()
    {
        ITEMS.clear();

        if(LMUtils.DEV_ENV)
        {
            ModularPipes.LOGGER.info("Network cleared");
        }
    }

    @SubscribeEvent
    public static void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        clear();
    }

    @SubscribeEvent
    public static void onTickEvent(TickEvent.WorldTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && event.world.isRemote && !ITEMS.isEmpty())
        {
            ITEMS.values().forEach(FOREACH_UPDATE);
        }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        if(ITEMS.isEmpty())
        {
            return;
        }

        partialTicks = event.getPartialTicks();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-FTBLibClient.playerX, -FTBLibClient.playerY, -FTBLibClient.playerZ);
        GlStateManager.disableLighting();
        RenderHelper.enableStandardItemLighting();
        ITEMS.values().forEach(FOREACH_RENDER);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}