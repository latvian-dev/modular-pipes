package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.PipeNetworkManagerBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class PipeNetworkManagerBlock extends Block {
	public PipeNetworkManagerBlock() {
		super(Properties.of(Material.METAL).strength(4F).requiresCorrectToolForDrops().sound(SoundType.METAL));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new PipeNetworkManagerBlockEntity();
	}
}
