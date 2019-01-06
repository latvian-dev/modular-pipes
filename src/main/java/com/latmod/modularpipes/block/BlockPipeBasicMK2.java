package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeBasicMK2;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipeBasicMK2 extends BlockPipeBasicMK1
{
	public BlockPipeBasicMK2(MapColor color)
	{
		super(color);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePipeBasicMK2();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("tile.modularpipes.pipe_basic_mk2.tooltip"));
	}
}