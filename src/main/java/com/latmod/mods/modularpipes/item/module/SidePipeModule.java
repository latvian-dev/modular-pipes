package com.latmod.mods.modularpipes.item.module;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class SidePipeModule extends PipeModule
{
	public EnumFacing side = null;

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

	@Nullable
	public TileEntity getFacingTile()
	{
		return pipe == null || side == null || !pipe.hasWorld() ? null : pipe.getWorld().getTileEntity(pipe.getPos().offset(side));
	}
}