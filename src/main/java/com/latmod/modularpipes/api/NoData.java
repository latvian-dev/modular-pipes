package com.latmod.modularpipes.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class NoData implements ModuleData
{
    public static final NoData INSTANCE = new NoData();

    private NoData()
    {
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
    }
}