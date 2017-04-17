package com.latmod.modularpipes.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * @author LatvianModder
 */
public class ItemBlockVariants extends ItemBlock
{
    public ItemBlockVariants(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }
}