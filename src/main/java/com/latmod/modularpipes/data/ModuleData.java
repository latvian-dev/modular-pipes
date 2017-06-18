package com.latmod.modularpipes.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public interface ModuleData
{
	void serializeNBT(NBTTagCompound nbt, boolean net);

	void deserializeNBT(NBTTagCompound nbt, boolean net);

	default boolean shouldSave()
	{
		return true;
	}
}