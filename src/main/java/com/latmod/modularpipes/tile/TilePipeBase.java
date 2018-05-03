package com.latmod.modularpipes.tile;

import com.feed_the_beast.ftblib.lib.tile.EnumSaveType;
import com.feed_the_beast.ftblib.lib.tile.TileBase;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.latmod.modularpipes.block.BlockPipeBase;
import com.latmod.modularpipes.data.IPipe;
import com.latmod.modularpipes.data.IPipeConnection;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class TilePipeBase extends TileBase implements IPipe
{
	private int connections = -1;

	@Override
	protected void writeData(NBTTagCompound nbt, EnumSaveType type)
	{
		if (type.save || connections != 0)
		{
			nbt.setByte("Connections", (byte) connections);
		}
	}

	@Override
	protected void readData(NBTTagCompound nbt, EnumSaveType type)
	{
		connections = nbt.getByte("Connections") & 0xFF;
	}

	@Override
	public void updateContainingBlockInfo()
	{
		super.updateContainingBlockInfo();
		connections = -1;
	}

	@Override
	public int getConnections()
	{
		if (connections == -1)
		{
			connections = 0;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

				if (tileEntity instanceof IPipeConnection && !((IPipeConnection) tileEntity).getPipeConnectionType(facing.getOpposite()).hasModule())
				{
					connections |= 1 << facing.getIndex();
				}
			}

			CommonUtils.notifyBlockUpdate(world, pos, null);
		}

		return connections;
	}

	public void onNeighborChange()
	{
		updateContainingBlockInfo();
		getConnections();
		CommonUtils.notifyBlockUpdate(world, pos, null);
	}

	@Override
	public boolean isPipeOpaque()
	{
		Block block = getBlockType();
		return block instanceof BlockPipeBase && ((BlockPipeBase) block).opaque;
	}

	@Override
	public void markDirty()
	{
		sendDirtyUpdate();
	}
}