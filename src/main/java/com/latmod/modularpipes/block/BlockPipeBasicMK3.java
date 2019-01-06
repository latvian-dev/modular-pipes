package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeBasicMK3;
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
public class BlockPipeBasicMK3 extends BlockPipeBasicMK2
{
	public BlockPipeBasicMK3(MapColor color)
	{
		super(color);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePipeBasicMK3();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("tile.modularpipes.pipe_basic_mk3.tooltip"));
	}
}