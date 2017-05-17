package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibPlugin;
import com.feed_the_beast.ftbl.api.IFTBLibPlugin;
import com.feed_the_beast.ftbl.api.events.FTBLibClientRegistryEvent;
import com.feed_the_beast.ftbl.api.events.FTBLibRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum FTBLibIntegration implements IFTBLibPlugin
{
    @FTBLibPlugin
    INSTANCE;

    /**
     * @author LatvianModder
     */
    public static FTBLibAPI API;

    @Override
    public void init(FTBLibAPI api)
    {
        API = api;
        MinecraftForge.EVENT_BUS.register(FTBLibIntegration.class);
    }

    @SubscribeEvent
    public static void registerCommon(FTBLibRegistryEvent event)
    {
        ModularPipesConfig.init(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerClient(FTBLibClientRegistryEvent event)
    {
        ModularPipesConfig.initClient(event.getRegistry());
    }
}