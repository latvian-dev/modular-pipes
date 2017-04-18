package com.latmod.modularpipes.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class FilterConfig implements INBTSerializable<NBTBase>
{
    private int filters;
    public boolean listEnabled = false;
    public boolean modeEnabled = false;
    public boolean blacklist = false;
    public boolean andMode = false;

    public FilterConfig enableList()
    {
        listEnabled = true;
        return this;
    }

    public FilterConfig enableMode()
    {
        modeEnabled = true;
        return this;
    }

    public void set(int f)
    {
        filters = f;
    }

    public int get()
    {
        return filters;
    }

    public boolean shouldSave()
    {
        return filters != 0 || listEnabled || modeEnabled;
    }

    @Override
    public NBTBase serializeNBT()
    {
        if(listEnabled || modeEnabled)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            if(filters != 0)
            {
                nbt.setInteger("Filters", filters);
            }
            if(listEnabled)
            {
                nbt.setBoolean("Blacklist", blacklist);
            }
            if(modeEnabled)
            {
                nbt.setBoolean("AndMode", andMode);
            }
            return nbt;
        }

        return new NBTTagInt(filters);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        if(nbt != null)
        {
            if(listEnabled || modeEnabled)
            {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                filters = tag.getInteger("Filters");
                blacklist = tag.getBoolean("Blacklist");
                andMode = tag.getBoolean("AndMode");
            }
            else
            {
                filters = ((NBTPrimitive) nbt).getInt();
            }
        }
    }
}