package com.latmod.modularpipes.block;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.latmod.modularpipes.ModularPipesItems;
import com.latmod.modularpipes.data.IModule;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.AxisAlignedBB;
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
	public static final AxisAlignedBB[] BOXES_MODULE = new AxisAlignedBB[7];

	static
	{
		double d0 = (SIZE - 1F) / 16D;
		double d1 = 1D - d0;
		BOXES_MODULE[0] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES_MODULE[1] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES_MODULE[2] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES_MODULE[3] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES_MODULE[4] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES_MODULE[5] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
	}

	public final PipeTier tier;

	public BlockModularPipe(String id, PipeTier t, boolean o)
	{
		super(id, MapColor.GRAY, o);
		tier = t;
	}

	@Override
	public Block getOppositeOpaque()
	{
		switch (tier)
		{
			case BASIC:
				return opaque ? ModularPipesItems.PIPE_MODULAR_BASIC : ModularPipesItems.PIPE_MODULAR_BASIC_OPAQUE;
			case IRON:
				return opaque ? ModularPipesItems.PIPE_MODULAR_IRON : ModularPipesItems.PIPE_MODULAR_IRON_OPAQUE;
			case GOLD:
				return opaque ? ModularPipesItems.PIPE_MODULAR_GOLD : ModularPipesItems.PIPE_MODULAR_GOLD_OPAQUE;
			case QUARTZ:
				return opaque ? ModularPipesItems.PIPE_MODULAR_QUARTZ : ModularPipesItems.PIPE_MODULAR_QUARTZ_OPAQUE;
			case LAPIS:
				return opaque ? ModularPipesItems.PIPE_MODULAR_LAPIS : ModularPipesItems.PIPE_MODULAR_LAPIS_OPAQUE;
			case ENDER:
				return opaque ? ModularPipesItems.PIPE_MODULAR_ENDER : ModularPipesItems.PIPE_MODULAR_ENDER_OPAQUE;
			case DIAMOND:
				return opaque ? ModularPipesItems.PIPE_MODULAR_DIAMOND : ModularPipesItems.PIPE_MODULAR_DIAMOND_OPAQUE;
			case STAR:
				return opaque ? ModularPipesItems.PIPE_MODULAR_STAR : ModularPipesItems.PIPE_MODULAR_STAR_OPAQUE;
		}

		return this;
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
		tooltip.add(I18n.format("tile.modularpipes.pipe_modular.slots", tier.config.modules));
		tooltip.add(I18n.format("tile.modularpipes.pipe.speed_boost", tier.speedString));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (player.isSneaking())
		{
			world.setBlockState(pos, getOppositeOpaque().getDefaultState());
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onRightClick(player, hand);
		}

		return true;
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		RayTraceResult ray = ClientUtils.MC.objectMouseOver;

		if (ray != null && ray.subHit != -1)
		{
			return (ray.subHit < 6 ? BOXES_64[1 << ray.subHit] : BOXES_MODULE[ray.subHit - 6]).offset(pos);
		}

		return BOXES_64[0].offset(pos);
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

		RayTraceResult ray = BOXES_64[0].calculateIntercept(start1, end1);

		if (ray != null)
		{
			double dist1 = ray.hitVec.squareDistanceTo(start1);

			if (dist >= dist1)
			{
				dist = dist1;
				ray1 = ray;
				ray1.subHit = -1;
			}
		}

		for (int i = 0; i < 6; i++)
		{
			if (pipe.getPipeConnectionType(EnumFacing.VALUES[i]).hasPipe())
			{
				ray = BOXES_64[1 << i].calculateIntercept(start1, end1);

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

			if (holdingModule || pipe.getPipeConnectionType(EnumFacing.VALUES[i]).hasModule())
			{
				ray = BOXES_MODULE[i].calculateIntercept(start1, end1);

				if (ray != null)
				{
					double dist1 = ray.hitVec.squareDistanceTo(start1);

					if (dist >= dist1)
					{
						dist = dist1;
						ray1 = ray;
						ray1.subHit = i + 6;
					}
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