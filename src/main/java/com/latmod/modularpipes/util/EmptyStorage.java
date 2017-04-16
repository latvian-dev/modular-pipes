package com.latmod.modularpipes.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class EmptyStorage implements Capability.IStorage<Object>
{
    private static final EmptyStorage INSTANCE = new EmptyStorage();

    public static <T> Capability.IStorage<T> getInstance()
    {
        return (Capability.IStorage<T>) INSTANCE;
    }

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
}