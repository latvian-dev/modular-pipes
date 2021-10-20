package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.block.entity.PipeSideData;
import dev.latvian.mods.modularpipes.client.PipeParticle;
import dev.latvian.mods.modularpipes.net.ParticleMessage;
import dev.latvian.mods.modularpipes.util.PipeNetwork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

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
	private LazyOptional<?> thisOptional = null;

	public LazyOptional<?> getThisOptional() {
		if (thisOptional == null) {
			thisOptional = LazyOptional.of(() -> this);
		}

		return thisOptional;
	}

	public void invalidateCaps() {
		if (thisOptional != null) {
			thisOptional.invalidate();
			thisOptional = null;
		}
	}

	public void writeData(CompoundTag nbt) {
	}

	public void readData(CompoundTag nbt) {
	}

	@Nullable
	public Component canInsert(Player player, InteractionHand hand) {
		return null;
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

	public boolean isThisCapability(Capability<?> capability) {
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CAP || isThisCapability(capability) ? getThisOptional().cast() : LazyOptional.empty();
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

	public void spawnParticle(PipeParticle type) {
		if (sideData.entity.hasLevel() && !sideData.entity.getLevel().isClientSide()) {
			double x = sideData.entity.getBlockPos().getX() + 0.5D + sideData.direction.getStepX() * 0.3D;
			double y = sideData.entity.getBlockPos().getY() + 0.5D + sideData.direction.getStepY() * 0.3D;
			double z = sideData.entity.getBlockPos().getZ() + 0.5D + sideData.direction.getStepZ() * 0.3D;

			Packet<?> packet = null;

			for (ServerPlayer player : ((ServerLevel) sideData.entity.getLevel()).players()) {
				if (type.canSee(player, x, y, z)) {
					if (packet == null) {
						packet = new ParticleMessage(x, y, z, type.ordinal()).toPacket();
					}

					player.connection.send(packet);
				}
			}
		}
	}
}