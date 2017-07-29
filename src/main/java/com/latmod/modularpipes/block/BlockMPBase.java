package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * @author LatvianModder
 */
public class BlockMPBase extends Block
{
	public BlockMPBase(String id, Material blockMaterialIn, MapColor blockMapColorIn)
	{
		super(blockMaterialIn, blockMapColorIn);
		setRegistryName(ModularPipes.MOD_ID + ':' + id);
		setUnlocalizedName(ModularPipes.MOD_ID + '.' + id);
		setCreativeTab(ModularPipes.TAB);
		setHardness(1.8F);
	}
}