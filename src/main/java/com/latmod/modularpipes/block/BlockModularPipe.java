package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.modularpipes.data.NodeType;
import com.latmod.modularpipes.data.TransportedItem;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockModularPipe extends BlockPipeBase
{
	public static final PropertyInteger CON_D = PropertyInteger.create("con_d", 0, 1);
	public static final PropertyInteger CON_U = PropertyInteger.create("con_u", 0, 1);
	public static final PropertyInteger CON_N = PropertyInteger.create("con_n", 0, 1);
	public static final PropertyInteger CON_S = PropertyInteger.create("con_s", 0, 1);
	public static final PropertyInteger CON_W = PropertyInteger.create("con_w", 0, 1);
	public static final PropertyInteger CON_E = PropertyInteger.create("con_e", 0, 1);
	public static final PropertyInteger[] CONNECTIONS = {CON_D, CON_U, CON_N, CON_S, CON_W, CON_E};

	public final EnumTier tier;

	public BlockModularPipe(String id, EnumTier t)
	{
		super(id, MapColor.GRAY);
		tier = t;
		setDefaultState(blockState.getBaseState()
				.withProperty(CON_D, 0)
				.withProperty(CON_U, 0)
				.withProperty(CON_N, 0)
				.withProperty(CON_S, 0)
				.withProperty(CON_W, 0)
				.withProperty(CON_E, 0));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, CON_D, CON_U, CON_N, CON_S, CON_W, CON_E);
	}

	@Deprecated
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileModularPipe(tier);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag)
	{
		if (tier == EnumTier.BASIC)
		{
			tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.tier_basic"));
		}
		else
		{
			tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.slots", tier.modules));
		}

		tooltip.add(StringUtils.translate("tile.modularpipes.pipe.speed_boost", StringUtils.formatDouble(tier.speed.getDouble()) + "x"));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onRightClick(playerIn, hand);
		}

		return true;
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			TileModularPipe pipe = (TileModularPipe) tileEntity;

			for (int i = 0; i < 6; i++)
			{
				state = state.withProperty(CONNECTIONS[i], (pipe.modules[i].hasModule() || pipe.canConnectTo(EnumFacing.VALUES[i])) ? 1 : 0);
			}
		}

		return state;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onNeighborChange();
		}
	}

	@Override
	@Nullable
	@Deprecated
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		TileModularPipe tile = (tileEntity instanceof TileModularPipe) ? (TileModularPipe) tileEntity : null;

		if (tile == null)
		{
			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
		}

		Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult ray1 = null;
		EntityPlayer player = worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5D, false);
		boolean holdingModule = player != null && (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemModule || player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemModule);
		double dist = Double.POSITIVE_INFINITY;

		for (int i = 0; i < BlockPipeBase.BOXES.length; i++)
		{
			if (i < 6 && !(holdingModule || tile.modules[i].hasModule() || tile.canConnectTo(EnumFacing.VALUES[i])))
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
	public boolean canConnectTo(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		return tileEntity instanceof TileModularPipe && ((TileModularPipe) tileEntity).canConnectTo(facing);
	}

	@Override
	public int getConnectionIdFromState(@Nullable IBlockState state)
	{
		if (state == null)
		{
			return 0;
		}

		int c = 0;

		for (int facing = 0; facing < 6; facing++)
		{
			c |= Math.min(1, state.getValue(CONNECTIONS[facing])) << facing;
		}

		return c;
	}

	@Override
	public NodeType getNodeType(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		return NodeType.TILES;
	}

	@Override
	public double getItemSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item)
	{
		return tier.speed.getDouble();
	}

	@Override
	public EnumFacing getItemDirection(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing source)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			return ((TileModularPipe) tileEntity).getItemDirection(item, source);
		}

		return source;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileModularPipe)
		{
			((TileModularPipe) tileEntity).onBroken();
		}

		super.breakBlock(worldIn, pos, state);
	}
}