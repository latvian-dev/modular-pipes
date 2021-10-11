package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * @author LatvianModder
 */
public class ModularPipeBlock extends BasePipeBlock {
	public final EnumMK tier;

	public ModularPipeBlock(EnumMK t) {
		super(Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).strength(0.6f).sound(SoundType.METAL));
		tier = t;
	}

	@Override
	public boolean isModular() {
		return true;
	}

	@Override
	public BasePipeBlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return tier.tileEntity.get();
	}

	// @Override
	// public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
	// 	return layer == BlockRenderLayer.TRANSLUCENT || super.canRenderInLayer(state, layer);
	// }


	@Override
	public int getLightValue(BlockState state, BlockGetter level, BlockPos pos) {
		return ModularPipes.PROXY.getPipeLightValue(level);
	}

	/*
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack stack = player.getHeldItem(hand);

		if (stack.getItem() instanceof ItemBlockPipe) {
			return false;
		} else if (super.onBlockActivated(state, world, pos, player, hand, hit)) {
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TilePipeModularMK1)) {
			return true;
		}
		TilePipeModularMK1 pipe = (TilePipeModularMK1) tileEntity;
		double dist = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
		Vec3d start = player.getEyePosition(1F);
		Vec3d look = player.getLookVec();
		Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
		BlockRayTraceResult ray = player.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
		Direction side = ray.getType() != RayTraceResult.Type.MISS && ray.subHit >= 0 && ray.subHit < 6 ? Direction.byIndex(ray.subHit) : null;

		if (side == null && ray.getType() != RayTraceResult.Type.MISS) {
			side = ray.getFace();
		}

		if (side == null) {
			return true;
		}

		if (player.isSneaking()) {
			Iterator<PipeModule> iterator = pipe.modules.iterator();

			while (iterator.hasNext()) {
				PipeModule module = iterator.next();

				if (module.isConnected(side) && module.canRemove(player)) {
					if (!world.isRemote) {
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
		if (lazyOptional.isPresent()) {
			if (pipe.modules.size() < tier.maxModules) {
				ItemStack stack1 = stack.copy();
				stack1.setCount(1);
				PipeModule module = lazyOptional.orElseThrow(NullPointerException::new);
				module.pipe = pipe;
				module.moduleItem = stack1;

				if (module.canInsert(player, side)) {
					pipe.modules.add(module);
					module.onInserted(player, side);

					if (!world.isRemote) {
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

		for (PipeModule module : pipe.modules) {
			if (module.isConnected(side) && module.onModuleRightClick(player, hand)) {
				return true;
			}
		}

		if (!world.isRemote) {
			if (player.isSneaking()) {
				List<TilePipeModularMK1> network = pipe.getPipeNetwork();

				player.sendMessage(new StringTextComponent("Network: " + world.getWorldInfo().getGameTime() + " [" + network.size() + "]"));

				for (TilePipeModularMK1 pipe1 : network) {
					player.sendMessage(new StringTextComponent(String.format("- %s#%08X", pipe1.getClass().getSimpleName(), pipe1.hashCode())));
				}
			} else {
				player.sendMessage(new StringTextComponent("Network: " + world.getWorldInfo().getGameTime()));

				for (Direction facing1 : Direction.values()) {
					CachedBlockEntity t = pipe.getTile(facing1);

					if (t.blockEntity != null) {
						player.sendMessage(new StringTextComponent(String.format("- %s#%08X [%d, %s]", t.blockEntity.getClass().getSimpleName(), t.blockEntity.hashCode(), t.distance, facing1.getName())));
					}
				}
			}

			//player.openGui(ModularPipes.INSTANCE, facing.getIndex(), world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
	 */

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
}