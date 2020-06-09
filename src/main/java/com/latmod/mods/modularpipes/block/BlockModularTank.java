package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.TileModularTankPart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockModularTank extends Block
{
	public BlockModularTank()
	{
		super(Block.Properties.create(Material.IRON, MaterialColor.BLUE_TERRACOTTA).sound(SoundType.METAL).hardnessAndResistance(0.6f));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new TileModularTankPart();
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		return player.getHeldItem(handIn).getItem() != ModularPipesItems.MODULAR_TANK;
	}
}