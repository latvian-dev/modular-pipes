package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.api.block.IBlockWithItem;
import com.feed_the_beast.ftbl.lib.block.ItemBlockBase;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

/**
 * @author LatvianModder
 */
public class BlockBase extends Block implements IBlockWithItem
{
    public BlockBase(String id, Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
        setRegistryName(ModularPipes.MOD_ID + ':' + id);
        setUnlocalizedName(ModularPipes.MOD_ID + '.' + id);
        setHardness(1.8F);
        setResistance(3F);
        setCreativeTab(ModularPipesCommon.TAB);
    }

    @Override
    public ItemBlock createItemBlock()
    {
        return new ItemBlockBase(this, false);
    }
}