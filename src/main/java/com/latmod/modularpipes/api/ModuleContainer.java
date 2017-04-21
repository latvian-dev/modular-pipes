package com.latmod.modularpipes.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface ModuleContainer
{
    IPipeNetwork getNetwork();

    default boolean hasModule()
    {
        return !getModule().isEmpty();
    }

    FilterConfig getFilterConfig();

    int getTick();

    Module getModule();

    TileEntity getTile();

    EnumFacing getFacing();

    ItemStack getItemStack();

    ModuleData getData();

    @Nullable
    default TileEntity getFacingTile()
    {
        return getTile().getWorld().getTileEntity(getTile().getPos().offset(getFacing()));
    }

    default boolean isRemote()
    {
        return getTile().getWorld().isRemote;
    }
}