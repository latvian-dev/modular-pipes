package dev.latvian.mods.modularpipes.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author LatvianModder
 */
public class TransportPipeBlockEntity extends BasePipeBlockEntity {
	public Direction end1 = null, end2 = null;

	public TransportPipeBlockEntity() {
		super(ModularPipesBlockEntities.TRANSPORT_PIPE);
	}

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		if (end1 != null) {
			nbt.putByte("end_1", (byte) end1.get3DDataValue());
		}

		if (end2 != null) {
			nbt.putByte("end_2", (byte) end2.get3DDataValue());
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		end1 = nbt.contains("end_1") ? Direction.from3DDataValue(nbt.getByte("end_1")) : null;
		end2 = nbt.contains("end_2") ? Direction.from3DDataValue(nbt.getByte("end_2")) : null;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
	}

	@Override
	public boolean isConnected(Direction facing) {
		return facing == end1 || facing == end2;
	}

	public CachedBlockEntity findNextOne(Direction from, int d) {
		if (end1 == null || end2 == null || end1 == end2) {
			return CachedBlockEntity.NONE;
		}

		if (from == end1) {
			BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(end2));

			if (tileEntity instanceof TransportPipeBlockEntity) {
				return ((TransportPipeBlockEntity) tileEntity).findNextOne(end2.getOpposite(), d + 1);
			} else if (tileEntity != null) {
				return new CachedBlockEntity(tileEntity, d + 1);
			}
		} else if (from == end2) {
			BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(end1));

			if (tileEntity instanceof TransportPipeBlockEntity) {
				return ((TransportPipeBlockEntity) tileEntity).findNextOne(end1.getOpposite(), d + 1);
			} else if (tileEntity != null) {
				return new CachedBlockEntity(tileEntity, d + 1);
			}
		}

		return CachedBlockEntity.NONE;
	}
}