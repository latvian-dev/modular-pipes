package com.latmod.modularpipes.block;

import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.data.IModule;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockModularPipe extends BlockPipeBase
{
	public final ModularPipesConfig.Tier tier;

	public BlockModularPipe(String id, ModularPipesConfig.Tier t, boolean o)
	{
		super(id, MapColor.GRAY, o);
		tier = t;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileModularPipe(tier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format("tile.modularpipes.pipe_modular.slots", tier.modules));
		tooltip.add(I18n.format("tile.modularpipes.pipe.speed_boost", tier.getSpeedString()));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onRightClick(player, hand);
		}

		return true;
	}

	@Override
	@Nullable
	@Deprecated
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TileModularPipe))
		{
			return super.collisionRayTrace(state, world, pos, start, end);
		}

		TileModularPipe pipe = (TileModularPipe) tileEntity;
		Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult ray1 = null;

		EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5D, false);
		boolean holdingModule = player != null && (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof IModule || player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof IModule);
		double dist = Double.POSITIVE_INFINITY;

		for (int i = 0; i < BlockPipeBase.BOXES.length; i++)
		{
			if (i > 0 && !(holdingModule || pipe.getPipeConnectionType(EnumFacing.VALUES[(i - 1) % 6]).hasPipe()))
			{
				continue;
			}

			RayTraceResult ray = BlockPipeBase.BOXES[i].calculateIntercept(start1, end1);

			if (ray != null)
			{
				double dist1 = ray.hitVec.squareDistanceTo(start1);

				if (dist >= dist1)
				{
					dist = dist1;
					ray1 = ray;
					ray1.subHit = i;
				}
			}
		}

		if (ray1 != null)
		{
			RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
			ray2.subHit = ray1.subHit;
			return ray2;
		}

		return null;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onBroken();
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT ? 15 : 0;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.SOLID || !opaque && layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
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
}