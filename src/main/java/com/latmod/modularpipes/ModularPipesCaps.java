package com.latmod.modularpipes;

import com.latmod.modularpipes.api.IPipeController;
import com.latmod.modularpipes.api.IPipeNetworkTile;
import com.latmod.modularpipes.api.Module;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesCaps
{
    @CapabilityInject(Module.class)
    public static Capability<Module> MODULE;

    @CapabilityInject(IPipeNetworkTile.class)
    public static Capability<Module> PIPE_NET_TILE;

    @CapabilityInject(IPipeController.class)
    public static Capability<Module> PIPE_CONTROLLER;

    private static final Capability.IStorage<Object> STORAGE = new Capability.IStorage<Object>()
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<Object> capability, Object instance, EnumFacing side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<Object> capability, Object instance, EnumFacing side, NBTBase nbt)
        {
        }
    };

    private static <T> Capability.IStorage<T> getStorage()
    {
        return (Capability.IStorage<T>) STORAGE;
    }

    public static void init()
    {
        CapabilityManager.INSTANCE.register(Module.class, getStorage(), () -> null);
        CapabilityManager.INSTANCE.register(IPipeNetworkTile.class, getStorage(), () -> null);
        CapabilityManager.INSTANCE.register(IPipeController.class, getStorage(), () -> null);
    }
}