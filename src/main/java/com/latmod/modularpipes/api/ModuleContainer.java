package com.latmod.modularpipes.api;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public interface ModuleContainer
{
    Module getModule();

    TileEntity getTile();

    EnumFacing getFacing();

    ItemStack getItemStack();

    ModuleData getData();
}