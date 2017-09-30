package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
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

@Mod(modid = ModularPipes.MOD_ID, name = ModularPipes.MOD_NAME, version = ModularPipes.VERSION, acceptedMinecraftVersions = "[1.12,)", dependencies = "required-after:" + FTBLibFinals.MOD_ID)
public class ModularPipes
{
	public static final String MOD_ID = "modularpipes";
	public static final String MOD_NAME = "Modular Pipes";
	public static final String VERSION = "@VERSION@";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@SidedProxy(serverSide = "com.latmod.modularpipes.ModularPipesCommon", clientSide = "com.latmod.modularpipes.client.ModularPipesClient")
	public static ModularPipesCommon PROXY;

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModularPipesItems.PIPE_MODULAR_STAR);
		}
	};

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ModularPipesConfig.sync();
		PROXY.preInit();
	}

	@EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		ServerPipeNetwork.clearAll();
	}
}