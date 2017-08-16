package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.util.DataStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public interface Module
{
	Module EMPTY = new Module()
	{
		@Override
		public boolean isEmptyModule()
		{
			return true;
		}
	};

	default boolean isEmptyModule()
	{
		return false;
	}

	default DataStorage createModuleData(ModuleContainer container)
	{
		return DataStorage.EMPTY;
	}

	default FilterConfig createFilterConfig(ModuleContainer container)
	{
		return new FilterConfig();
	}

	default boolean insertInPipe(ModuleContainer container, EntityPlayer player)
	{
		return true;
	}

	default void removeFromPipe(ModuleContainer container, EntityPlayer player)
	{
	}

	default void pipeBroken(ModuleContainer container)
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