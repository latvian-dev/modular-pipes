package dev.latvian.mods.modularpipes.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author LatvianModder
 */
public class TileBase extends BlockEntity {
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public TileBase(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void writeData(CompoundTag nbt) {
	}

	public void readData(CompoundTag nbt) {
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		writeData(nbt);
		return super.save(nbt);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt) {
		super.load(state, nbt);
		readData(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		load(state, tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return new ClientboundBlockEntityDataPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		readData(packet.getTag());
	}
}
