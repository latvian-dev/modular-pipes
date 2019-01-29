package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.ModularPipes;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import com.latmod.mods.modularpipes.item.ItemBlockPipe;
import com.latmod.mods.modularpipes.tile.CachedTileEntity;
import com.latmod.mods.modularpipes.tile.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
		return ModularPipes.PROXY.getPipeLightValue();
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

		return super.getPackedLightmapCoords(state, source, pos);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.getItem() instanceof ItemBlockPipe)
		{
			return false;
		}
		else if (super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ))
		{
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TilePipeModularMK1))
		{
			return true;
		}
		TilePipeModularMK1 pipe = (TilePipeModularMK1) tileEntity;
		double dist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d start = player.getPositionEyes(1F);
		Vec3d look = player.getLookVec();
		Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
		RayTraceResult ray = player.world.rayTraceBlocks(start, end, false, true, false);
		EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

		if (side == null && ray != null)
		{
			side = ray.sideHit;
		}

		if (side == null)
		{
			return true;
		}

		if (player.isSneaking())
		{
			Iterator<PipeModule> iterator = pipe.modules.iterator();

			while (iterator.hasNext())
			{
				PipeModule module = iterator.next();

				if (module.isConnected(side) && module.canRemove(player))
				{
					if (!world.isRemote)
					{
						ItemHandlerHelper.giveItemToPlayer(player, module.stack);
					}

					module.onRemoved(player);
					iterator.remove();
					tileEntity.markDirty();
					world.notifyBlockUpdate(pos, state, state, 11);
					PipeNetwork.get(world).refresh();
					return true;
				}
			}
		}
		else if (stack.hasCapability(PipeModule.CAP, null))
		{
			if (pipe.modules.size() < tier.maxModules)
			{
				ItemStack stack1 = stack.copy();
				stack1.setCount(1);
				PipeModule module = stack1.getCapability(PipeModule.CAP, null);

				if (module != null)
				{
					module.pipe = pipe;
					module.stack = stack1;

					if (module.canInsert(player, side))
					{
						pipe.modules.add(module);
						module.onInserted(player, side);

						if (!world.isRemote)
						{
							stack.shrink(1);
						}

						tileEntity.markDirty();
						world.notifyBlockUpdate(pos, state, state, 11);
						PipeNetwork.get(world).refresh();
						return true;
					}
				}
			}

			player.sendStatusMessage(new TextComponentString("Can't insert any more modules!"), true); //LANG
			return true;
		}

		for (PipeModule module : pipe.modules)
		{
			if (module.isConnected(side) && module.onModuleRightClick(player, hand))
			{
				return true;
			}
		}

		if (!world.isRemote)
		{
			if (player.isSneaking())
			{
				HashSet<TilePipeModularMK1> set = new HashSet<>();
				pipe.getNetwork(set);

				player.sendMessage(new TextComponentString("Network: " + world.getTotalWorldTime() + " [" + set.size() + "]"));

				for (TilePipeModularMK1 pipe1 : set)
				{
					player.sendMessage(new TextComponentString(String.format("- %s#%08X", pipe1.getClass().getSimpleName(), pipe1.hashCode())));
				}
			}
			else
			{
				player.sendMessage(new TextComponentString("Network: " + world.getTotalWorldTime()));

				for (EnumFacing facing1 : EnumFacing.VALUES)
				{
					CachedTileEntity t = pipe.getTile(facing1);

					if (t.tile != null)
					{
						player.sendMessage(new TextComponentString(String.format("- %s#%08X [%d, %s]", t.tile.getClass().getSimpleName(), t.tile.hashCode(), t.distance, facing1.getName())));
					}
				}
			}

			//player.openGui(ModularPipes.INSTANCE, facing.getIndex(), world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TilePipeModularMK1)
			{
				((TilePipeModularMK1) tileEntity).dropItems();
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	@Nullable
	@Deprecated
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TilePipeModularMK1))
		{
			return super.collisionRayTrace(state, world, pos, start, end);
		}

		TilePipeModularMK1 tile = (TilePipeModularMK1) tileEntity;

		Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult ray1 = null;
		EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5D, false);
		boolean holdingModule = tile.modules.size() < tier.maxModules && player != null && (player.getHeldItem(EnumHand.MAIN_HAND).hasCapability(PipeModule.CAP, null) || player.getHeldItem(EnumHand.OFF_HAND).hasCapability(PipeModule.CAP, null));
		double dist = Double.POSITIVE_INFINITY;

		for (int i = 0; i < BlockPipeBase.BOXES.length; i++)
		{
			if (i < 6 && !(holdingModule || tile.isConnected(EnumFacing.VALUES[i])))
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
			RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
			ray2.subHit = ray1.subHit;
			return ray2;
		}

		return null;
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;

		if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
		{
			return BOXES[ray.subHit].offset(pos);
		}

		return super.getSelectedBoundingBox(state, worldIn, pos);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity != null)
		{
			tileEntity.updateContainingBlockInfo();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add("Max modules: " + tier.maxModules); //LANG
	}
}