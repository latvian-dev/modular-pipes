package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import dev.latvian.mods.modularpipes.net.ParticleMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class SidedPipeModule extends PipeModule {
	public Direction side = null;
	private Optional<BlockEntity> cachedEntity = Optional.empty();

	@Override
	public void writeData(CompoundTag nbt) {
		if (side != null) {
			nbt.putByte("Side", (byte) side.get3DDataValue());
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		side = nbt.contains("Side", Constants.NBT.TAG_ANY_NUMERIC) ? Direction.from3DDataValue(nbt.getByte("side")) : null;
	}

	@Override
	public void onInserted(Player player, @Nullable Direction facing) {
		side = facing;
	}

	@Override
	public boolean isConnected(Direction facing) {
		return facing == side;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		cachedEntity = Optional.empty();
	}

	@Nullable
	public BlockEntity getFacingTile() {
		if (!cachedEntity.isPresent()) {
			cachedEntity = Optional.ofNullable(pipe == null || side == null || !pipe.hasLevel() ? null : pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(side)));
		}

		return cachedEntity.orElse(null);
	}

	@Override
	public void spawnParticle(int type) {
		if (pipe.hasLevel() && !pipe.getLevel().isClientSide()) {
			double x = pipe.getBlockPos().getX() + 0.5D + (side == null ? 0D : side.getStepX() * 0.3D);
			double y = pipe.getBlockPos().getY() + 0.5D + (side == null ? 0D : side.getStepY() * 0.3D);
			double z = pipe.getBlockPos().getZ() + 0.5D + (side == null ? 0D : side.getStepZ() * 0.3D);
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getLevel().dimension())), new ParticleMessage(pipe.getBlockPos(), null, type));
		}
	}
}