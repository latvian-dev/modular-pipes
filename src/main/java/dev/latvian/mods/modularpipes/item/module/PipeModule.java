package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import dev.latvian.mods.modularpipes.block.entity.PipeSideData;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import dev.latvian.mods.modularpipes.net.ParticleMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public abstract class PipeModule implements ICapabilityProvider {
	@CapabilityInject(PipeModule.class)
	public static Capability<PipeModule> CAP;

	public PipeSideData sideData;
	public ItemStack moduleItem = ItemStack.EMPTY;

	private Optional<BlockEntity> cachedEntity = Optional.empty();
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public void writeData(CompoundTag nbt) {
	}

	public void readData(CompoundTag nbt) {
	}

	public boolean canInsert(Player player, InteractionHand hand) {
		return true;
	}

	public void onInserted(Player player, InteractionHand hand) {
	}

	public boolean canRemove(Player player, InteractionHand hand) {
		return true;
	}

	public void onRemoved(Player player, InteractionHand hand) {
	}

	public void onPipeBroken() {
	}

	public void updateModule() {
	}

	public void clearCache() {
		cachedEntity = Optional.empty();
	}

	public boolean onModuleRightClick(Player player, InteractionHand hand) {
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CAP ? thisOptional.cast() : LazyOptional.empty();
	}

	public final void refreshNetwork() {
		if (sideData.entity.hasLevel()) {
			PipeNetwork.get(sideData.entity.getLevel()).refresh();
		}
	}

	public String toString() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return moduleItem.getItem().getRegistryName() + (nbt.isEmpty() ? "" : ("+" + nbt));
	}

	@Nullable
	public BlockEntity getFacingTile() {
		if (!cachedEntity.isPresent()) {
			cachedEntity = Optional.ofNullable(!sideData.entity.hasLevel() ? null : sideData.entity.getLevel().getBlockEntity(sideData.entity.getBlockPos().relative(sideData.direction)));
		}

		return cachedEntity.orElse(null);
	}

	public void spawnParticle(int type) {
		if (sideData.entity.hasLevel() && !sideData.entity.getLevel().isClientSide()) {
			double x = sideData.entity.getBlockPos().getX() + 0.5D + sideData.direction.getStepX() * 0.3D;
			double y = sideData.entity.getBlockPos().getY() + 0.5D + sideData.direction.getStepY() * 0.3D;
			double z = sideData.entity.getBlockPos().getZ() + 0.5D + sideData.direction.getStepZ() * 0.3D;
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, sideData.entity.getLevel().dimension())), new ParticleMessage(sideData.entity.getBlockPos(), null, type));
		}
	}
}