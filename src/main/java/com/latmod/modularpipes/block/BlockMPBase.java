package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.api.game.IBlockWithItem;
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
public class BlockMPBase extends Block implements IBlockWithItem
{
	public BlockMPBase(String id, Material blockMaterialIn, MapColor blockMapColorIn)
	{
		super(blockMaterialIn, blockMapColorIn);
		setRegistryName(ModularPipes.MOD_ID + ':' + id);
		setUnlocalizedName(ModularPipes.MOD_ID + '.' + id);
		setCreativeTab(ModularPipesCommon.TAB);
		setHardness(1.8F);
	}

	@Override
	public ItemBlock createItemBlock()
	{
		return new ItemBlockBase(this);
	}
}