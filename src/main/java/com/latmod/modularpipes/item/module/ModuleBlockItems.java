package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.data.Module;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModuleBlockItems extends Module
{
    @Override
    @Nullable
    public EnumFacing changeItemDirection(EnumFacing source, TransportedItem item)
    {
        return source;
    }
}