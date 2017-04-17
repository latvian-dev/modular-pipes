package com.latmod.modularpipes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModularPipes.MOD_ID, name = "Modular Pipes", version = "0.0.0", useMetadata = true)
public class ModularPipes
{
    public static final String MOD_ID = "modularpipes";
    public static final Logger LOGGER = LogManager.getLogger("ModularPipes");

    @SidedProxy(serverSide = "com.latmod.modularpipes.ModularPipesCommon", clientSide = "com.latmod.modularpipes.client.ModularPipesClient")
    public static ModularPipesCommon PROXY;

    public static final SimpleNetworkWrapper NET = new SimpleNetworkWrapper(MOD_ID);

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        PROXY.onPreInit();
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        PROXY.onInit();
    }
}