package com.latmod.modularpipes.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class TilePipeTransport extends TilePipeBase
{
	public EnumFacing end1 = null, end2 = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (end1 != null)
		{
			nbt.setByte("end_1", (byte) end1.getIndex());
		}

		if (end2 != null)
		{
			nbt.setByte("end_2", (byte) end2.getIndex());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		end1 = nbt.hasKey("end_1") ? EnumFacing.byIndex(nbt.getByte("end_1")) : null;
		end2 = nbt.hasKey("end_2") ? EnumFacing.byIndex(nbt.getByte("end_2")) : null;
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
	}

	@Override
	public boolean isConnected(EnumFacing facing)
	{
		return facing == end1 || facing == end2;
	}

	public CachedTileEntity findNextOne(EnumFacing from, int d)
	{
		if (end1 == null || end2 == null || end1 == end2)
		{
			return CachedTileEntity.NONE;
		}

		if (from == end1)
		{
			TileEntity tileEntity = world.getTileEntity(pos.offset(end2));

			if (tileEntity instanceof TilePipeTransport)
			{
				return ((TilePipeTransport) tileEntity).findNextOne(end2.getOpposite(), d + 1);
			}
			else if (tileEntity != null)
			{
				return new CachedTileEntity(tileEntity, d + 1);
			}
		}
		else if (from == end2)
		{
			TileEntity tileEntity = world.getTileEntity(pos.offset(end1));

			if (tileEntity instanceof TilePipeTransport)
			{
				return ((TilePipeTransport) tileEntity).findNextOne(end1.getOpposite(), d + 1);
			}
			else if (tileEntity != null)
			{
				return new CachedTileEntity(tileEntity, d + 1);
			}
		}

		return CachedTileEntity.NONE;
	}
}