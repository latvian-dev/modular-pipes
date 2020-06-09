package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.TileModularStoragePart;
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
public class BlockModularStorage extends Block
{
	public BlockModularStorage()
	{
		super(Properties.create(Material.IRON, MaterialColor.BROWN).hardnessAndResistance(0.6f).sound(SoundType.METAL));
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
		return new TileModularStoragePart();
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		return player.getHeldItem(handIn).getItem() != ModularPipesItems.MODULAR_STORAGE;
	}
}