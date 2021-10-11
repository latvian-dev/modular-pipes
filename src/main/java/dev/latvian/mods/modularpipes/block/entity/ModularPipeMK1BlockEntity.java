package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.EnumMK;
import dev.latvian.mods.modularpipes.item.ItemKey;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipeMK1BlockEntity extends BasePipeBlockEntity implements IEnergyStorage {
	public final CachedBlockEntity[] cachedTiles = new CachedBlockEntity[6];
	public List<PipeItem> items = new ArrayList<>(0);
	public List<PipeModule> modules = new ArrayList<>(0);
	public Object2IntOpenHashMap<ItemKey> itemDirections = new Object2IntOpenHashMap<>(0);
	public int storedPower = 0;
	private int powerOutputIndex = -1;
	private List<ModularPipeMK1BlockEntity> cachedNetwork = null;

	public ModularPipeMK1BlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE_MK1);
	}

	public ModularPipeMK1BlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (!modules.isEmpty()) {
			ListTag list = new ListTag();

			for (PipeModule module : modules) {
				CompoundTag nbt1 = module.moduleItem.serializeNBT();
				CompoundTag nbt2 = new CompoundTag();
				module.writeData(nbt2);

				if (!nbt2.isEmpty()) {
					nbt1.put("module", nbt2);
				}

				list.add(nbt1);
			}

			nbt.put("modules", list);
		}

		if (!items.isEmpty()) {
			ListTag list = new ListTag();

			for (PipeItem item : items) {
				list.add(item.serializeNBT());
			}

			nbt.put("items", list);
		}

		if (storedPower > 0) {
			nbt.putInt("power", storedPower);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);

		ListTag list = nbt.getList("modules", Constants.NBT.TAG_COMPOUND);
		modules = new ArrayList<>(list.size());

		for (int i = 0; i < list.size(); i++) {
			CompoundTag nbt1 = list.getCompound(i);
			ItemStack stack = ItemStack.of(nbt1);
			PipeModule module = stack.getCapability(PipeModule.CAP, null).orElse(null);

			if (module != null) {
				module.pipe = this;
				module.moduleItem = stack;
				module.readData(nbt1.getCompound("module"));
				modules.add(module);
			}
		}

		list = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
		items = new ArrayList<>(list.size());

		for (int i = 0; i < list.size(); i++) {
			CompoundTag nbt1 = list.getCompound(i);
			PipeItem item = new PipeItem();
			item.deserializeNBT(nbt1);

			if (!item.stack.isEmpty()) {
				items.add(item);
			}
		}

		storedPower = nbt.getInt("power");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		if (capability == CapabilityEnergy.ENERGY && side == null) {
			return thisOptional.cast();
		}

		for (PipeModule module : modules) {
			LazyOptional<T> t = module.getCapability(capability, side);

			if (t.isPresent()) {
				return t.cast();
			}
		}

		return super.getCapability(capability, side);
	}

	public EnumMK getMK() {
		return EnumMK.MK1;
	}

	@Override
	public void moveItem(PipeItem item) {
		item.pos += item.speed;
	}

	public void tickPipe() {
		if (!level.isClientSide() && !modules.isEmpty()) {
			for (PipeModule module : modules) {
				if (module.canUpdate()) {
					module.updateModule();
				}
			}
		}

		if (!level.isClientSide() && storedPower > 0 && level.getGameTime() % 5L == 0L) {
			if (powerOutputIndex == -1) {
				powerOutputIndex = hashCode() % 6;

				if (powerOutputIndex < 0) {
					powerOutputIndex = 6 - powerOutputIndex;
				}
			}

			CachedBlockEntity tileEntity = getBlockEntity(Direction.from3DDataValue(powerOutputIndex));

			if (tileEntity.blockEntity instanceof ModularPipeMK1BlockEntity) {
				ModularPipeMK1BlockEntity pipe = (ModularPipeMK1BlockEntity) tileEntity.blockEntity;

				if (Math.abs(storedPower - pipe.storedPower) > 1) {
					int a = (storedPower + pipe.storedPower) / 2;

					storedPower = a;
					setChanged();
					sync = false;

					pipe.storedPower = a;
					pipe.setChanged();
					pipe.sync = false;
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
			sync = false;
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();
		Arrays.fill(cachedTiles, null);
		itemDirections = new Object2IntOpenHashMap<>(0);

		for (PipeModule module : modules) {
			module.clearCache();
		}

		cachedNetwork = null;
	}

	public CachedBlockEntity getBlockEntity(Direction facing) {
		int f = facing.get3DDataValue();

		if (cachedTiles[f] == null) {
			cachedTiles[f] = CachedBlockEntity.NONE;

			BlockPos pos1 = worldPosition.relative(facing);
			BlockEntity tileEntity = level.getBlockEntity(pos1);

			if (tileEntity instanceof ModularPipeMK1BlockEntity) {
				if (canPipesConnect()) {
					cachedTiles[f] = new CachedBlockEntity(tileEntity, 1);
				}
			} else if (tileEntity instanceof TransportPipeBlockEntity) {
				if (canPipesConnect()) {
					cachedTiles[f] = ((TransportPipeBlockEntity) tileEntity).findNextOne(facing.getOpposite(), 1);

					if (cachedTiles[f].blockEntity == this) {
						cachedTiles[f] = CachedBlockEntity.NONE;
					}
				}
			} else if (tileEntity != null) {
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

	@Override
	public boolean isConnected(Direction facing) {
		BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(facing));

		if (tileEntity instanceof BasePipeBlockEntity) {
			return canPipesConnect();
		}

		for (PipeModule module : modules) {
			if (module.isConnected(facing)) {
				return true;
			}
		}

		return false;
	}

	public void dropItems() {
		for (PipeItem item : items) {
			Block.popResource(level, worldPosition, item.stack);
		}

		for (PipeModule module : modules) {
			module.onPipeBroken();
			Block.popResource(level, worldPosition, module.moduleItem);
		}
	}

	public List<ModularPipeMK1BlockEntity> getPipeNetwork() {
		if (cachedNetwork == null) {
			HashSet<ModularPipeMK1BlockEntity> set = new HashSet<>();
			getNetwork(set);
			cachedNetwork = new ArrayList<>(set);

			if (cachedNetwork.size() > 1) {
				cachedNetwork.sort(Comparator.comparingDouble(value -> worldPosition.distSqr(value.worldPosition)));
			}

			cachedNetwork = ModularPipesUtils.optimize(cachedNetwork);
		}

		return cachedNetwork;
	}

	private void getNetwork(HashSet<ModularPipeMK1BlockEntity> set) {
		for (Direction facing : Direction.values()) {
			BlockEntity tileEntity = getBlockEntity(facing).blockEntity;

			if (tileEntity instanceof ModularPipeMK1BlockEntity && !set.contains(tileEntity)) {
				set.add((ModularPipeMK1BlockEntity) tileEntity);
				((ModularPipeMK1BlockEntity) tileEntity).getNetwork(set);
			}
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return storedPower;
	}

	@Override
	public int getMaxEnergyStored() {
		return ModularPipesConfig.pipes.max_energy_stored;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}