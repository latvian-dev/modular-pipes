package com.latmod.modularpipes.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public interface IModule
{
	default boolean canInsert(ModuleContainer container, EntityPlayer player)
	{
		return true;
	}

	default void onInserted(ModuleContainer container, EntityPlayer player)
	{
	}

	default boolean canRemove(ModuleContainer container, EntityPlayer player)
	{
		return true;
	}

	default void onRemoved(ModuleContainer container, EntityPlayer player)
	{
	}

	default void onPipeBroken(ModuleContainer container)
	{
	}

	default void updateModule(ModuleContainer container)
	{
	}

	default boolean onModuleRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
	{
		return false;
	}
}