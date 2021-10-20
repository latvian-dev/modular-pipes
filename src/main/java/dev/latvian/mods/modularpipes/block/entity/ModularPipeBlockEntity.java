package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.util.CachedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class ModularPipeBlockEntity extends PipeBlockEntity {
	public final CachedBlockEntity[] cachedTiles = new CachedBlockEntity[6];
	private final int powerOutputIndex = -1;

	public ModularPipeBlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE.get());
	}

	public void tickPipe() {
		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.updateModule();
			}
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();
		Arrays.fill(cachedTiles, null);

		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.clearCache();
			}
		}
	}

	public CachedBlockEntity getBlockEntity(Direction facing) {
		int f = facing.get3DDataValue();

		if (cachedTiles[f] == null) {
			cachedTiles[f] = CachedBlockEntity.NONE;

			BlockPos pos1 = worldPosition.relative(facing);
			BlockEntity tileEntity = level.getBlockEntity(pos1);

			if (tileEntity != null) {
				cachedTiles[f] = new CachedBlockEntity(tileEntity, 1);
			}

			if (cachedTiles[f].blockEntity != null) {
				for (int i = 0; i < 6; i++) {
					if (i != f && cachedTiles[i] != null && cachedTiles[f].blockEntity == cachedTiles[i].blockEntity) {
						if (cachedTiles[f].distance < cachedTiles[i].distance) {
							cachedTiles[i] = CachedBlockEntity.NONE;
						} else {
							cachedTiles[f] = CachedBlockEntity.NONE;
						}

						break;
					}
				}
			}
		}

		return cachedTiles[f];
	}
}