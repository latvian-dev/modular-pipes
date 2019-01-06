package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipeBase;
import com.latmod.modularpipes.tile.TilePipeBasicMK1;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipeBasicMK1 extends BlockPipeBase
{
	public BlockPipeBasicMK1(MapColor color)
	{
		super(color);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePipeBasicMK1();
	}

	@Override
	public boolean isConnected(TilePipeBase pipe, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		IBlockState state = world.getBlockState(pos.offset(facing));

		if (state.getBlock() != this && state.getBlock() instanceof BlockPipeBasicMK1)
		{
			return false;
		}

		return super.isConnected(pipe, world, pos, facing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("tile.modularpipes.pipe_basic_mk1.tooltip"));
	}
}