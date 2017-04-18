package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.TransportedItem;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModuleBlockItems extends Module
{
    @Nullable
    public EnumFacing changeItemDirection(EnumFacing source, TransportedItem item)
    {
        return source;
    }
}