package com.latmod.mods.modularpipes.item.module;

import com.latmod.mods.modularpipes.net.MessageParticle;
import com.latmod.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class SidedPipeModule extends PipeModule
{
	public EnumFacing side = null;
	private Optional<TileEntity> cachedEntity = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		if (side != null)
		{
			nbt.setByte("side", (byte) side.getIndex());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		side = nbt.hasKey("side", Constants.NBT.TAG_ANY_NUMERIC) ? EnumFacing.byIndex(nbt.getByte("side")) : null;
	}

	@Override
	public void onInserted(EntityPlayer player, @Nullable EnumFacing facing)
	{
		side = facing;
	}

	@Override
	public boolean isConnected(EnumFacing facing)
	{
		return facing == side;
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		cachedEntity = null;
	}

	@Nullable
	public TileEntity getFacingTile()
	{
		if (cachedEntity == null)
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
			ModularPipesNet.NET.sendToAllAround(new MessageParticle(pipe.getPos(), side, type), new NetworkRegistry.TargetPoint(pipe.getWorld().provider.getDimension(), x, y, z, 24D));
		}
	}
}