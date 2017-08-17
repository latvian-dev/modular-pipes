package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.block.BlockBase;
import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * @author LatvianModder
 */
public class BlockMPBase extends BlockBase
{
	public BlockMPBase(String id, Material material, MapColor color)
	{
		super(ModularPipes.MOD_ID, id, material, color);
		setCreativeTab(ModularPipes.TAB);
	}
}