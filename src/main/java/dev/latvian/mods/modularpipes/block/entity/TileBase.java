package dev.latvian.mods.modularpipes.block.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author LatvianModder
 */
public class TileBase extends BlockEntity {
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public TileBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void writeData(CompoundNBT nbt) {
	}

	public void readData(CompoundNBT nbt) {
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		writeData(nbt);
		return super.write(nbt);
	}

	@Override
	public void read(CompoundNBT nbt) {
		super.read(nbt);
		readData(nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		read(tag);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		writeData(nbt);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SUpdateTileEntityPacket packet) {
		readData(packet.getNbtCompound());
	}
}
