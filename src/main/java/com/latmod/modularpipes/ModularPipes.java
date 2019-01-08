package com.latmod.modularpipes;

import com.latmod.modularpipes.block.PipeSkin;
import com.latmod.modularpipes.data.IModule;
import com.latmod.modularpipes.gui.ModularPipesGuiHandler;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.PipeNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(
		modid = ModularPipes.MOD_ID,
		name = ModularPipes.MOD_NAME,
		version = ModularPipes.VERSION,
		acceptedMinecraftVersions = "[1.12,)",
		dependencies = "required-after:itemfilters"
)
public class ModularPipes
{
	public static final String MOD_ID = "modularpipes";
	public static final String MOD_NAME = "Modular Pipes";
	public static final String VERSION = "0.0.0.modularpipes";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Mod.Instance(MOD_ID)
	public static ModularPipes INSTANCE;

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModularPipesItems.PIPE_MODULAR_MK1);
		}
	};

	@CapabilityInject(IModule.class)
	public static Capability<IModule> MODULE_CAP;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ModularPipesConfig.sync();

		CapabilityManager.INSTANCE.register(IModule.class, new Capability.IStorage<IModule>()
		{
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<IModule> capability, IModule instance, EnumFacing side)
			{
				return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
			}

			@Override
			public void readNBT(Capability<IModule> capability, IModule instance, EnumFacing side, NBTBase nbt)
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
			public NBTBase writeNBT(Capability<PipeNetwork> capability, PipeNetwork instance, EnumFacing side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<PipeNetwork> capability, PipeNetwork instance, EnumFacing side, NBTBase nbt)
			{
			}
		}, () -> null);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, ModularPipesGuiHandler.INSTANCE);
		ModularPipesNet.init();

		PipeSkin.register("white", "item.fireworksCharge.white", new ResourceLocation("minecraft:blocks/concrete_powder_white"));
		PipeSkin.register("orange", "item.fireworksCharge.orange", new ResourceLocation("minecraft:blocks/concrete_powder_orange"));
		PipeSkin.register("magenta", "item.fireworksCharge.magenta", new ResourceLocation("minecraft:blocks/concrete_powder_magenta"));
		PipeSkin.register("light_blue", "item.fireworksCharge.lightBlue", new ResourceLocation("minecraft:blocks/concrete_powder_light_blue"));
		PipeSkin.register("yellow", "item.fireworksCharge.yellow", new ResourceLocation("minecraft:blocks/concrete_powder_yellow"));
		PipeSkin.register("lime", "item.fireworksCharge.lime", new ResourceLocation("minecraft:blocks/concrete_powder_lime"));
		PipeSkin.register("pink", "item.fireworksCharge.pink", new ResourceLocation("minecraft:blocks/concrete_powder_pink"));
		PipeSkin.register("gray", "item.fireworksCharge.gray", new ResourceLocation("minecraft:blocks/concrete_powder_gray"));
		PipeSkin.register("silver", "item.fireworksCharge.silver", new ResourceLocation("minecraft:blocks/concrete_powder_silver"));
		PipeSkin.register("cyan", "item.fireworksCharge.cyan", new ResourceLocation("minecraft:blocks/concrete_powder_cyan"));
		PipeSkin.register("purple", "item.fireworksCharge.purple", new ResourceLocation("minecraft:blocks/concrete_powder_purple"));
		PipeSkin.register("blue", "item.fireworksCharge.blue", new ResourceLocation("minecraft:blocks/concrete_powder_blue"));
		PipeSkin.register("brown", "item.fireworksCharge.brown", new ResourceLocation("minecraft:blocks/concrete_powder_brown"));
		PipeSkin.register("green", "item.fireworksCharge.green", new ResourceLocation("minecraft:blocks/concrete_powder_green"));
		PipeSkin.register("red", "item.fireworksCharge.red", new ResourceLocation("minecraft:blocks/concrete_powder_red"));
		PipeSkin.register("black", "item.fireworksCharge.black", new ResourceLocation("minecraft:blocks/concrete_powder_black"));
		PipeSkin.register("brick", "tile.brick.name", new ResourceLocation("minecraft:blocks/brick"));
		PipeSkin.register("ice", "tile.ice.name", new ResourceLocation("minecraft:blocks/ice_packed"));
		PipeSkin.register("melon", "tile.melon.name", new ResourceLocation("minecraft:blocks/melon_top"));
	}
}