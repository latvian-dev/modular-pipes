package com.latmod.mods.modularpipes;

import com.latmod.mods.modularpipes.client.ModularPipesClient;
import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.net.ModularPipesNet;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(ModularPipes.MOD_ID)
public class ModularPipes
{
	public static final String MOD_ID = "modularpipes";
	public static final ItemGroup TAB = new ItemGroup(MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModularPipesItems.PIPE_MODULAR_MK1);
		}
	};
	//	@SidedProxy(serverSide = "com.latmod.mods.modularpipes.ModularPipesCommon", clientSide = "com.latmod.mods.modularpipes.client.ModularPipesClient")
	public static ModularPipesCommon PROXY;

	public ModularPipes()
	{
		PROXY = DistExecutor.runForDist(() -> ModularPipesClient::new, () -> ModularPipesCommon::new);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModularPipesEventHandler::registerBlocks);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModularPipesEventHandler::registerItems);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, ModularPipesEventHandler::registerTiles);
	}

	public void loadComplete(FMLLoadCompleteEvent event)
	{
		CapabilityManager.INSTANCE.register(PipeModule.class, new Capability.IStorage<PipeModule>()
		{
			@Nullable
			@Override
			public INBT writeNBT(Capability<PipeModule> capability, PipeModule instance, Direction side)
			{
				return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
			}

			@Override
			public void readNBT(Capability<PipeModule> capability, PipeModule instance, Direction side, INBT nbt)
			{
				if (nbt != null && instance instanceof INBTSerializable)
				{
					((INBTSerializable) instance).deserializeNBT(nbt);
				}
			}
		}, () -> null);

		CapabilityManager.INSTANCE.register(PipeNetwork.class, new Capability.IStorage<PipeNetwork>()
		{
			@Nullable
			@Override
			public INBT writeNBT(Capability<PipeNetwork> capability, PipeNetwork instance, Direction side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<PipeNetwork> capability, PipeNetwork instance, Direction side, INBT nbt)
			{
			}
		}, () -> null);

		ModularPipesNet.init();
	}
}