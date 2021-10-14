package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipeBlockEntity extends PipeBlockEntity {
	public final CachedBlockEntity[] cachedTiles = new CachedBlockEntity[6];
	public int storedEnergy = 0;
	private int powerOutputIndex = -1;
	private List<ModularPipeBlockEntity> cachedNetwork = null;

	public ModularPipeBlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE.get());
	}

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (storedEnergy > 0) {
			nbt.putInt("Energy", storedEnergy);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		storedEnergy = nbt.getInt("Energy");
	}

	public void tickPipe() {
		if (!level.isClientSide()) {
			for (PipeSideData data : sideData) {
				if (data.module != null) {
					data.module.updateModule();
				}
			}
		}

		if (!level.isClientSide() && storedEnergy > 0 && level.getGameTime() % 5L == 0L) {
			if (powerOutputIndex == -1) {
				powerOutputIndex = hashCode() % 6;

				if (powerOutputIndex < 0) {
					powerOutputIndex = 6 - powerOutputIndex;
				}
			}

			CachedBlockEntity tileEntity = getBlockEntity(Direction.from3DDataValue(powerOutputIndex));

			if (tileEntity.blockEntity instanceof ModularPipeBlockEntity) {
				ModularPipeBlockEntity pipe = (ModularPipeBlockEntity) tileEntity.blockEntity;

				if (Math.abs(storedEnergy - pipe.storedEnergy) > 1) {
					int a = (storedEnergy + pipe.storedEnergy) / 2;

					storedEnergy = a;
					setChanged();

					pipe.storedEnergy = a;
					pipe.setChanged();
				}
			}

			powerOutputIndex++;
		}

		if (items.isEmpty()) {
			return;
		}

		for (PipeItem item : items.size() == 1 ? Collections.singletonList(items.get(0)) : new ArrayList<>(items)) {
			item.age++;

			if (item.to == 6 || item.from == 6) {
				item.age = Integer.MAX_VALUE;
			} else if (item.pos >= 1F) {
				/*
				IItemHandler handler = getInventory(world, item.to);

				if (handler instanceof IPipeItemHandler && ((IPipeItemHandler) handler).insertPipeItem(item.copyForTransfer(null), false))
				{
					markDirty();
					sync = false;
				}
				else
				{
					ItemStack stack = ItemHandlerHelper.insertItem(handler, item.stack, world.isRemote);

					if (!stack.isEmpty())
					{
						IPipeItemHandler opposite = getPipeItemHandler(Direction.VALUES[item.to].getOpposite());

						if (opposite != null)
						{
							if (!opposite.insertPipeItem(item.copyForTransfer(stack), false) && !world.isRemote)
							{
								InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
							}
						}
						else if (!world.isRemote)
						{
							InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
						}

						markDirty();

						if (handler == opposite)
						{
							sync = false;
						}
					}
				}
				*/

				item.age = Integer.MAX_VALUE;
			}
		}

		if (items.removeIf(PipeItem.IS_DEAD)) {
			setChanged();
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();
		Arrays.fill(cachedTiles, null);

		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.clearCache();
			}
		}

		cachedNetwork = null;
	}

	public CachedBlockEntity getBlockEntity(Direction facing) {
		int f = facing.get3DDataValue();

		if (cachedTiles[f] == null) {
			cachedTiles[f] = CachedBlockEntity.NONE;

			BlockPos pos1 = worldPosition.relative(facing);
			BlockEntity tileEntity = level.getBlockEntity(pos1);

			if (tileEntity != null) {
				cachedTiles[f] = new CachedBlockEntity(tileEntity, 1);
			}

			if (cachedTiles[f].blockEntity != null) {
				for (int i = 0; i < 6; i++) {
					if (i != f && cachedTiles[i] != null && cachedTiles[f].blockEntity == cachedTiles[i].blockEntity) {
						if (cachedTiles[f].distance < cachedTiles[i].distance) {
							cachedTiles[i] = CachedBlockEntity.NONE;
						} else {
							cachedTiles[f] = CachedBlockEntity.NONE;
						}

						break;
					}
				}
			}
		}

		return cachedTiles[f];
	}

	public List<ModularPipeBlockEntity> getPipeNetwork() {
		if (cachedNetwork == null) {
			HashSet<ModularPipeBlockEntity> set = new HashSet<>();
			getNetwork(set);
			cachedNetwork = new ArrayList<>(set);

			if (cachedNetwork.size() > 1) {
				cachedNetwork.sort(Comparator.comparingDouble(value -> worldPosition.distSqr(value.worldPosition)));
			}

			cachedNetwork = ModularPipesUtils.optimize(cachedNetwork);
		}

		return cachedNetwork;
	}

	private void getNetwork(HashSet<ModularPipeBlockEntity> set) {
		for (Direction facing : Direction.values()) {
			BlockEntity tileEntity = getBlockEntity(facing).blockEntity;

			if (tileEntity instanceof ModularPipeBlockEntity && !set.contains(tileEntity)) {
				set.add((ModularPipeBlockEntity) tileEntity);
				((ModularPipeBlockEntity) tileEntity).getNetwork(set);
			}
		}
	}
}