package com.latmod.modularpipes.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public interface ModuleData extends INBTSerializable<NBTTagCompound>
{
	default boolean shouldSave()
	{
		return true;
	}
}