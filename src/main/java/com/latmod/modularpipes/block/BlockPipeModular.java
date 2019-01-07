package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.item.ItemBlockPipe;
import com.latmod.modularpipes.tile.TilePipeBase;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * @author LatvianModder
 */
public class BlockPipeModular extends BlockPipeBase
{
	public final EnumMK tier;

	public BlockPipeModular(EnumMK t)
	{
		super(MapColor.LIGHT_BLUE);
		tier = t;
	}

	@Override
	public boolean isModular()
	{
		return true;
	}

	@Override
	public TilePipeBase createTileEntity(World world, IBlockState state)
	{
		return tier.tileEntity.get();
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.TRANSLUCENT || super.canRenderInLayer(state, layer);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT ? 15 : 0;
	}

	@Override
	@Deprecated
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT)
		{
			int result = source.getCombinedLight(pos, 15);
			int skylight = (result >> 16) & 0xFFFF;
			return (skylight << 16) | (15 << 4);
		}
		else
		{
			return super.getPackedLightmapCoords(state, source, pos);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (player.getHeldItem(hand).getItem() instanceof ItemBlockPipe)
		{
			return false;
		}
		else if (super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ))
		{
			return true;
		}
		else if (!world.isRemote)
		{
			player.openGui(ModularPipes.INSTANCE, facing.getIndex(), world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
}