package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PipeNetworkManagerBlockEntity extends BlockEntity implements IEnergyStorage {
	public int energy;
	private final List<ModularPipeBlockEntity> cachedNetwork = null;
	private LazyOptional<?> thisOptional;

	public PipeNetworkManagerBlockEntity() {
		super(ModularPipesBlockEntities.PIPE_NETWORK_MANAGER.get());
	}

	@Override
	public void load(BlockState state, CompoundTag nbt) {
		super.load(state, nbt);
		energy = nbt.getInt("Energy");
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		super.save(nbt);

		if (energy > 0) {
			nbt.putInt("Energy", energy);
		}

		return nbt;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			return getThisOptional().cast();
		}

		return super.getCapability(cap, side);
	}

	public LazyOptional<?> getThisOptional() {
		if (thisOptional == null) {
			thisOptional = LazyOptional.of(() -> this);
		}

		return thisOptional;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();

		if (thisOptional != null) {
			thisOptional.invalidate();
			thisOptional = null;
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = Math.min(ModularPipesConfig.MAX_ENERGY_STORED - energy, Math.min(ModularPipesConfig.MAX_ENERGY_TRANSFER, maxReceive));

		if (!simulate) {
			energy += energyReceived;
		}

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = Math.min(energy, Math.min(ModularPipesConfig.MAX_ENERGY_TRANSFER, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
		}

		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return ModularPipesConfig.MAX_ENERGY_STORED;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	/*
	public List<ModularPipeBlockEntity> getPipeNetwork() {
		if (cachedNetwork == null) {
			HashSet<ModularPipeBlockEntity> set = new HashSet<>();
			getNetwork(set);
			cachedNetwork = new ArrayList<>(set);

			if (cachedNetwork.size() > 1) {
				cachedNetwork.sort(Comparator.comparingDouble(value -> worldPosition.distSqr(value.getBlockPos())));
			}

			cachedNetwork = ModularPipesUtils.optimize(cachedNetwork);
		}

		return cachedNetwork;
	}

	private void getNetwork(HashSet<ModularPipeBlockEntity> set) {
		for (Direction facing : Direction.values()) {
			BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(facing));

			if (tileEntity instanceof ModularPipeBlockEntity && !set.contains(tileEntity)) {
				set.add((ModularPipeBlockEntity) tileEntity);
				((ModularPipeBlockEntity) tileEntity).getNetwork(set);
			}
		}
	}
	 */
}
