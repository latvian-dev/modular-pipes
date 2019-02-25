package com.latmod.mods.modularpipes.item.module;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class SidePipeModule extends PipeModule
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
}