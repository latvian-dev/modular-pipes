package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.tile.EnumSaveType;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public interface ModuleData
{
	void serializeNBT(NBTTagCompound nbt, EnumSaveType type);

	void deserializeNBT(NBTTagCompound nbt, EnumSaveType type);

	default boolean shouldSave()
	{
		return true;
	}
}