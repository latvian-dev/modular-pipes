package com.latmod.mods.modularpipes.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;

/**
 * @author LatvianModder
 */
public class TileController extends TileBase implements ITickable
{
	private boolean hasError = false;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		nbt.setBoolean("Error", hasError);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		hasError = nbt.getBoolean("Error");
	}

	@Override
	public void update()
	{
	}

	public boolean hasError()
	{
		return hasError || isInvalid();
	}

	public void onRightClick(EntityPlayer player, EnumHand hand, EnumFacing facing)
	{
		hasError = !hasError;
		markDirty();
	}
}