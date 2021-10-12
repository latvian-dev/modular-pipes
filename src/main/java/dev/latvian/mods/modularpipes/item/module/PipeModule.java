package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.block.entity.BaseModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import dev.latvian.mods.modularpipes.net.ParticleMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PipeModule implements ICapabilityProvider {
	@CapabilityInject(PipeModule.class)
	public static Capability<PipeModule> CAP;

	public BaseModularPipeBlockEntity pipe = null;
	public ItemStack moduleItem = ItemStack.EMPTY;
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public void writeData(CompoundTag nbt) {
	}

	public void readData(CompoundTag nbt) {
	}

	public boolean canInsert(Player player, @Nullable Direction facing) {
		return true;
	}

	public void onInserted(Player player, @Nullable Direction facing) {
	}

	public boolean canRemove(Player player) {
		return true;
	}

	public void onRemoved(Player player) {
	}

	public void onPipeBroken() {
	}

	public boolean canUpdate() {
		return false;
	}

	public void updateModule() {
	}

	public void clearCache() {
	}

	public boolean onModuleRightClick(Player player, InteractionHand hand) {
		return false;
	}

	public boolean isConnected(Direction facing) {
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CAP ? thisOptional.cast() : LazyOptional.empty();
	}

	public final void refreshNetwork() {
		if (pipe != null && pipe.hasLevel()) {
			PipeNetwork.get(pipe.getLevel()).refresh();
		}
	}

	public String toString() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return moduleItem.getItem().getRegistryName() + (nbt.isEmpty() ? "" : ("+" + nbt));
	}

	public void spawnParticle(int type) {
		if (pipe.hasLevel() && !pipe.getLevel().isClientSide()) {
			double x = pipe.getBlockPos().getX() + 0.5D;
			double y = pipe.getBlockPos().getY() + 0.5D;
			double z = pipe.getBlockPos().getZ() + 0.5D;
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getLevel().dimension())), new ParticleMessage(pipe.getBlockPos(), null, type));
		}
	}
}