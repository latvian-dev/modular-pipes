package com.latmod.modularpipes.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class NoData implements ModuleData
{
    public static final NoData INSTANCE = new NoData()
    {
        @Override
        public boolean shouldSave()
        {
            return false;
        }
    };

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