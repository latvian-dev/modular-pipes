package com.latmod.modularpipes.item;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;
import net.minecraft.item.Item;

/**
 * @author LatvianModder
 */
public class ItemBase extends Item
{
    public ItemBase(String id)
    {
        setUnlocalizedName(ModularPipes.MOD_ID + '.' + id);
        setRegistryName(ModularPipes.MOD_ID + ':' + id);
        setCreativeTab(ModularPipesCommon.TAB);
    }
}