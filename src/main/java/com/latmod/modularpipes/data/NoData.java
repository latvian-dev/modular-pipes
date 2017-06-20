package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.tile.EnumSaveType;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class NoData implements ModuleData
{
	public static final NoData INSTANCE = new NoData()
	{
		@Override
		public boolean shouldSave()
		{
			return false;
		}
	};

	private NoData()
	{
	}

	@Override
	public void serializeNBT(NBTTagCompound nbt, EnumSaveType type)
	{
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt, EnumSaveType type)
	{
	}
}