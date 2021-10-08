package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

/**
 * @author LatvianModder
 */
public class TilePipeTransport extends TilePipeBase {
	public Direction end1 = null, end2 = null;

	public TilePipeTransport() {
		super(ModularPipesTiles.PIPE_TRANSPORT);
	}

	@Override
	public void writeData(CompoundNBT nbt) {
		super.writeData(nbt);

		if (end1 != null) {
			nbt.putByte("end_1", (byte) end1.getIndex());
		}

		if (end2 != null) {
			nbt.putByte("end_2", (byte) end2.getIndex());
		}
	}

	@Override
	public void readData(CompoundNBT nbt) {
		super.readData(nbt);
		end1 = nbt.contains("end_1") ? Direction.byIndex(nbt.getByte("end_1")) : null;
		end2 = nbt.contains("end_2") ? Direction.byIndex(nbt.getByte("end_2")) : null;
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SUpdateTileEntityPacket packet) {
		super.onDataPacket(net, packet);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
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
			TileEntity tileEntity = world.getTileEntity(pos.offset(end2));

			if (tileEntity instanceof TilePipeTransport) {
				return ((TilePipeTransport) tileEntity).findNextOne(end2.getOpposite(), d + 1);
			} else if (tileEntity != null) {
				return new CachedBlockEntity(tileEntity, d + 1);
			}
		} else if (from == end2) {
			TileEntity tileEntity = world.getTileEntity(pos.offset(end1));

			if (tileEntity instanceof TilePipeTransport) {
				return ((TilePipeTransport) tileEntity).findNextOne(end1.getOpposite(), d + 1);
			} else if (tileEntity != null) {
				return new CachedBlockEntity(tileEntity, d + 1);
			}
		}

		return CachedBlockEntity.NONE;
	}
}