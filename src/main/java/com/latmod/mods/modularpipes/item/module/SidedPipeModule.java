package com.latmod.mods.modularpipes.item.module;

import com.latmod.mods.modularpipes.net.MessageParticle;
import com.latmod.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class SidedPipeModule extends PipeModule
{
	public Direction side = null;
	private Optional<TileEntity> cachedEntity = Optional.empty();

	@Override
	public void writeData(CompoundNBT nbt)
	{
		if (side != null)
		{
			nbt.putByte("side", (byte) side.getIndex());
		}
	}

	@Override
	public void readData(CompoundNBT nbt)
	{
		side = nbt.contains("side", Constants.NBT.TAG_ANY_NUMERIC) ? Direction.byIndex(nbt.getByte("side")) : null;
	}

	@Override
	public void onInserted(PlayerEntity player, @Nullable Direction facing)
	{
		side = facing;
	}

	@Override
	public boolean isConnected(Direction facing)
	{
		return facing == side;
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		cachedEntity = Optional.empty();
	}

	@Nullable
	public TileEntity getFacingTile()
	{
		if (!cachedEntity.isPresent())
		{
			cachedEntity = Optional.ofNullable(pipe == null || side == null || !pipe.hasWorld() ? null : pipe.getWorld().getTileEntity(pipe.getPos().offset(side)));
		}

		return cachedEntity.orElse(null);
	}

	@Override
	public void spawnParticle(int type)
	{
		if (pipe.hasWorld() && !pipe.getWorld().isRemote)
		{
			double x = pipe.getPos().getX() + 0.5D + (side == null ? 0D : side.getXOffset() * 0.3D);
			double y = pipe.getPos().getY() + 0.5D + (side == null ? 0D : side.getYOffset() * 0.3D);
			double z = pipe.getPos().getZ() + 0.5D + (side == null ? 0D : side.getZOffset() * 0.3D);
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getWorld().getDimension().getType())), new MessageParticle(pipe.getPos(), null, type));
		}
	}
}