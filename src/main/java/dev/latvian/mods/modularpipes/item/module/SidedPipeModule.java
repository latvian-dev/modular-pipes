package dev.latvian.mods.modularpipes.item.module;

import dev.latvian.mods.modularpipes.net.MessageParticle;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
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
			cachedEntity = Optional.ofNullable(pipe == null || side == null || !pipe.hasWorld() ? null : pipe.getWorld().getTileEntity(pipe.getPos().offset(side)));
		}

		return cachedEntity.orElse(null);
	}

	@Override
	public void spawnParticle(int type) {
		if (pipe.hasWorld() && !pipe.getWorld().isRemote) {
			double x = pipe.getPos().getX() + 0.5D + (side == null ? 0D : side.getXOffset() * 0.3D);
			double y = pipe.getPos().getY() + 0.5D + (side == null ? 0D : side.getYOffset() * 0.3D);
			double z = pipe.getPos().getZ() + 0.5D + (side == null ? 0D : side.getZOffset() * 0.3D);
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getWorld().getDimension().getType())), new MessageParticle(pipe.getPos(), null, type));
		}
	}
}