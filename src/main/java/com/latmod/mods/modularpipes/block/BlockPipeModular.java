package com.latmod.mods.modularpipes.block;

import com.latmod.mods.modularpipes.ModularPipes;
import com.latmod.mods.modularpipes.item.ItemBlockPipe;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.tile.CachedTileEntity;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TilePipeBase;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

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
		super(Block.Properties.create(Material.IRON, MaterialColor.LIGHT_BLUE).hardnessAndResistance(0.6f).sound(SoundType.METAL));
		tier = t;
	}

	@Override
	public boolean isModular()
	{
		return true;
	}

	@Override
	public TilePipeBase createTileEntity(BlockState state, IBlockReader world)
	{
		return tier.tileEntity.get();
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
	{
		return layer == BlockRenderLayer.TRANSLUCENT || super.canRenderInLayer(state, layer);
	}

	@Override
	public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos)
	{
		return ModularPipes.PROXY.getPipeLightValue(world);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (stack.getItem() instanceof ItemBlockPipe)
		{
			return false;
		}
		else if (super.onBlockActivated(state, world, pos, player, hand, hit))
		{
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TilePipeModularMK1))
		{
			return true;
		}
		TilePipeModularMK1 pipe = (TilePipeModularMK1) tileEntity;
		double dist = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
		Vec3d start = player.getEyePosition(1F);
		Vec3d look = player.getLookVec();
		Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
		BlockRayTraceResult ray = player.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
		Direction side = ray.getType() != RayTraceResult.Type.MISS && ray.subHit >= 0 && ray.subHit < 6 ? Direction.byIndex(ray.subHit) : null;

		if (side == null && ray.getType() != RayTraceResult.Type.MISS)
		{
			side = ray.getFace();
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
						ItemHandlerHelper.giveItemToPlayer(player, module.moduleItem);
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
		LazyOptional<PipeModule> lazyOptional = stack.getCapability(PipeModule.CAP);
		if (lazyOptional.isPresent())
		{
			if (pipe.modules.size() < tier.maxModules)
			{
				ItemStack stack1 = stack.copy();
				stack1.setCount(1);
				PipeModule module = lazyOptional.orElseThrow(NullPointerException::new);
				module.pipe = pipe;
				module.moduleItem = stack1;

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

			player.sendStatusMessage(new StringTextComponent("Can't insert any more modules!"), true); //LANG
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
				List<TilePipeModularMK1> network = pipe.getPipeNetwork();

				player.sendMessage(new StringTextComponent("Network: " + world.getWorldInfo().getGameTime() + " [" + network.size() + "]"));

				for (TilePipeModularMK1 pipe1 : network)
				{
					player.sendMessage(new StringTextComponent(String.format("- %s#%08X", pipe1.getClass().getSimpleName(), pipe1.hashCode())));
				}
			}
			else
			{
				player.sendMessage(new StringTextComponent("Network: " + world.getWorldInfo().getGameTime()));

				for (Direction facing1 : Direction.values())
				{
					CachedTileEntity t = pipe.getTile(facing1);

					if (t.tile != null)
					{
						player.sendMessage(new StringTextComponent(String.format("- %s#%08X [%d, %s]", t.tile.getClass().getSimpleName(), t.tile.hashCode(), t.distance, facing1.getName())));
					}
				}
			}

			//player.openGui(ModularPipes.INSTANCE, facing.getIndex(), world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	//	@Override
	//	public void breakBlock(World world, BlockPos pos, BlockState state)
	//	{
	//		if (!world.isRemote)
	//		{
	//			TileEntity tileEntity = world.getTileEntity(pos);
	//
	//			if (tileEntity instanceof TilePipeModularMK1)
	//			{
	//				((TilePipeModularMK1) tileEntity).dropItems();
	//			}
	//		}
	//
	//		super.breakBlock(world, pos, state);
	//	}


	//	@Override
	//	@Nullable
	//	@Deprecated
	//	public RayTraceResult collisionRayTrace(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
	//	{
	//		TileEntity tileEntity = world.getTileEntity(pos);
	//
	//		if (!(tileEntity instanceof TilePipeModularMK1))
	//		{
	//			return super.collisionRayTrace(state, world, pos, start, end);
	//		}
	//
	//		TilePipeModularMK1 tile = (TilePipeModularMK1) tileEntity;
	//
	//		Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
	//		Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
	//		RayTraceResult ray1 = null;
	//		PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5D, false);
	//		boolean holdingModule = tile.modules.size() < tier.maxModules && player != null && (player.getHeldItem(Hand.MAIN_HAND).hasCapability(PipeModule.CAP, null) || player.getHeldItem(Hand.OFF_HAND).hasCapability(PipeModule.CAP, null));
	//		double dist = Double.POSITIVE_INFINITY;
	//
	//		for (int i = 0; i < BlockPipeBase.BOXES.length; i++)
	//		{
	//			if (i < 6 && !(holdingModule || tile.isConnected(Direction.VALUES[i])))
	//			{
	//				continue;
	//			}
	//
	//			RayTraceResult ray = BlockPipeBase.BOXES[i].calculateIntercept(start1, end1);
	//
	//			if (ray != null)
	//			{
	//				double dist1 = ray.hitVec.squareDistanceTo(start1);
	//
	//				if (dist >= dist1)
	//				{
	//					dist = dist1;
	//					ray1 = ray;
	//					ray1.subHit = i;
	//				}
	//			}
	//		}
	//
	//		if (ray1 != null)
	//		{
	//			RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
	//			ray2.subHit = ray1.subHit;
	//			return ray2;
	//		}
	//
	//		return null;
	//	}

	//	@Override
	//	@Deprecated
	//	@OnlyIn(Dist.CLIENT)
	//	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos)
	//	{
	//		RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
	//
	//		if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
	//		{
	//			return BOXES[ray.subHit].offset(pos);
	//		}
	//
	//		return super.getSelectedBoundingBox(state, worldIn, pos);
	//	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity != null)
		{
			tileEntity.updateContainingBlockInfo();
		}
	}
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	//	{
	//		tooltip.add("Max modules: " + tier.maxModules); //LANG
	//	}
}