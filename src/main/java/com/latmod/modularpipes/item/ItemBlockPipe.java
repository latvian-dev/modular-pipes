package com.latmod.modularpipes.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * @author LatvianModder
 */
public class ItemBlockPipe extends ItemBlock
{
    public ItemBlockPipe(Block block)
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