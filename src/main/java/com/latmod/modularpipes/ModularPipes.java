package com.latmod.modularpipes;

import com.latmod.modularpipes.data.ServerPipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModularPipes.MOD_ID, name = "Modular Pipes", useMetadata = true, acceptedMinecraftVersions = "[1.12,)", dependencies = "required-after:ftbl")
public class ModularPipes
{
	public static final String MOD_ID = "modularpipes";
	public static final Logger LOGGER = LogManager.getLogger("ModularPipes");

	@SidedProxy(serverSide = "com.latmod.modularpipes.ModularPipesCommon", clientSide = "com.latmod.modularpipes.client.ModularPipesClient")
	public static ModularPipesCommon PROXY;

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, 7);
		}
	};

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		PROXY.preInit();
	}

	@EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		ServerPipeNetwork.clearAll();
	}
}