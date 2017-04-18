package com.latmod.modularpipes;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

/**
 * @author LatvianModder
 */
public class ModularPipesEventHandler
{
    @SubscribeEvent
    public static void onTickEvent(TickEvent.WorldTickEvent event)
    {
        if(event.world.provider.getDimension() == 0 && event.phase == TickEvent.Phase.END)
        {
            PipeNetwork.INSTANCE.update();
        }
    }

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event)
    {
        if(event.getWorld().provider.getDimension() != 0)
        {
            return;
        }

        PipeNetwork.INSTANCE = new PipeNetwork();
        PipeNetwork.INSTANCE.clear();

        File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/modularpipes.dat");

        if(!file.exists())
        {
            return;
        }

        NBTTagCompound nbt;

        try
        {
            nbt = CompressedStreamTools.read(file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        PipeNetwork.INSTANCE.deserializeNBT(nbt);
    }

    @SubscribeEvent
    public static void onWorldSaved(WorldEvent.Save event)
    {
        if(event.getWorld().provider.getDimension() != 0)
        {
            return;
        }

        File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/modularpipes.dat");

        try
        {
            CompressedStreamTools.write(PipeNetwork.INSTANCE.serializeNBT(), file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.player instanceof EntityPlayerMP)
        {
            PipeNetwork.INSTANCE.syncOnLogin((EntityPlayerMP) event.player);
        }
    }
}