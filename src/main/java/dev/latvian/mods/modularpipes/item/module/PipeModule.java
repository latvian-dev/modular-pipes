package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK1;
import dev.latvian.mods.modularpipes.net.MessageParticle;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PipeModule implements ICapabilityProvider {
	public TilePipeModularMK1 pipe = null;
	public ItemStack moduleItem = ItemStack.EMPTY;
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public void writeData(CompoundTag nbt) {
	}

	public void readData(CompoundTag nbt) {
	}

	public boolean canInsert(PlayerEntity player, @Nullable Direction facing) {
		return true;
	}

	public void onInserted(PlayerEntity player, @Nullable Direction facing) {
	}

	public boolean canRemove(PlayerEntity player) {
		return true;
	}

	public void onRemoved(PlayerEntity player) {
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

	public boolean onModuleRightClick(PlayerEntity player, Hand hand) {
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
		if (pipe != null && pipe.hasWorld()) {
			PipeNetwork.get(pipe.getWorld()).refresh();
		}
	}

	public String toString() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return moduleItem.getItem().getRegistryName() + (nbt.isEmpty() ? "" : ("+" + nbt));
	}

	public void spawnParticle(int type) {
		if (pipe.hasWorld() && !pipe.getWorld().isRemote) {
			double x = pipe.getPos().getX() + 0.5D;
			double y = pipe.getPos().getY() + 0.5D;
			double z = pipe.getPos().getZ() + 0.5D;
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getWorld().getDimension().getType())), new MessageParticle(pipe.getPos(), null, type));
		}
	}
}