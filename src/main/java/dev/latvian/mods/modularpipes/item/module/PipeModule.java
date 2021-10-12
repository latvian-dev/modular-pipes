package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
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
public class PipeModule implements ICapabilityProvider {
	@CapabilityInject(PipeModule.class)
	public static Capability<PipeModule> CAP;

	public ModularPipeBlockEntity pipe = null;
	public ItemStack moduleItem = ItemStack.EMPTY;
	public Direction side = Direction.DOWN;

	private Optional<BlockEntity> cachedEntity = Optional.empty();
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public void writeData(CompoundTag nbt) {
		nbt.putByte("Side", (byte) side.get3DDataValue());
	}

	public void readData(CompoundTag nbt) {
		side = Direction.from3DDataValue(nbt.getByte("Side"));
	}

	public boolean canInsert(Player player, InteractionHand hand) {
		return true;
	}

	public void onInserted(Player player, InteractionHand hand) {
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
		if (pipe != null && pipe.hasLevel()) {
			PipeNetwork.get(pipe.getLevel()).refresh();
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
			cachedEntity = Optional.ofNullable(pipe == null || side == null || !pipe.hasLevel() ? null : pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(side)));
		}

		return cachedEntity.orElse(null);
	}

	public void spawnParticle(int type) {
		if (pipe.hasLevel() && !pipe.getLevel().isClientSide()) {
			double x = pipe.getBlockPos().getX() + 0.5D + (side == null ? 0D : side.getStepX() * 0.3D);
			double y = pipe.getBlockPos().getY() + 0.5D + (side == null ? 0D : side.getStepY() * 0.3D);
			double z = pipe.getBlockPos().getZ() + 0.5D + (side == null ? 0D : side.getStepZ() * 0.3D);
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getLevel().dimension())), new ParticleMessage(pipe.getBlockPos(), null, type));
		}
	}
}