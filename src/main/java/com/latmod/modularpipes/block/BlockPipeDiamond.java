package com.latmod.modularpipes.block;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.gui.ModularPipesGuiHandler;
import com.latmod.modularpipes.tile.TilePipeBase;
import com.latmod.modularpipes.tile.TilePipeDiamond;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockPipeDiamond extends BlockPipeBase
{
	public BlockPipeDiamond()
	{
		super(MapColor.DIAMOND);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePipeDiamond();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (Block.getBlockFromItem(player.getHeldItem(hand).getItem()) instanceof BlockPipeBase)
		{
			return false;
		}

		if (!world.isRemote)
		{
			player.openGui(ModularPipes.INSTANCE, ModularPipesGuiHandler.DIAMOND_PIPE, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	public boolean isConnected(TilePipeBase pipe, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		if (pipe instanceof TilePipeDiamond && ((TilePipeDiamond) pipe).inventories[facing.getIndex()].filter.getItem() == ItemFiltersAPI.NULL_ITEM)
		{
			return false;
		}

		return super.isConnected(pipe, world, pos, facing);
	}
}