package com.latmod.modularpipes.item;

import com.feed_the_beast.ftbl.lib.item.ItemBase;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;

/**
 * @author LatvianModder
 */
public class ItemMPBase extends ItemBase
{
    public ItemMPBase(String id)
    {
        super(ModularPipes.MOD_ID + ':' + id);
        setCreativeTab(ModularPipesCommon.TAB);
    }
}