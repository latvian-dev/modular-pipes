package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.latmod.modularpipes.ModularPipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class ModularPipesPlayerData implements INBTSerializable<NBTTagCompound>
{
	public static final ResourceLocation ID = new ResourceLocation(ModularPipes.MOD_ID, "data");

	public static ModularPipesPlayerData get(IForgePlayer player)
	{
		return player.getData().get(ID);
	}

	public final ConfigBoolean devMode = new ConfigBoolean(false)
	{
		@Override
		public void setBoolean(boolean v)
		{
			super.setBoolean(v);

			if (player.isOnline())
			{
				PipeNetwork.get(player.getPlayer().getEntityWorld()).networkUpdated = true;
			}
		}
	};

	private final IForgePlayer player;

	public ModularPipesPlayerData(IForgePlayer p)
	{
		player = p;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("Dev", devMode.getBoolean());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		devMode.setBoolean(nbt.getBoolean("Dev"));
	}
}