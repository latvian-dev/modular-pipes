package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.TileModularTankPart;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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
public class BlockModularTank extends Block
{
	public BlockModularTank()
	{
		super(Material.IRON, MapColor.BLUE_STAINED_HARDENED_CLAY);
		setHardness(0.6F);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileModularTankPart();
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return player.getHeldItem(hand).getItem() != ModularPipesItems.MODULAR_TANK;
	}
}