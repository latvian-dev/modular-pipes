package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.block.entity.ModularPipesBlockEntities;
import dev.latvian.mods.modularpipes.client.ModularPipesClient;
import dev.latvian.mods.modularpipes.item.ModularPipesItems;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

@Mod(ModularPipes.MOD_ID)
public class ModularPipes {
	public static final String MOD_ID = "modularpipes";
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModularPipesItems.MODULAR_PIPE_MK1.get());
		}
	};

	public static ModularPipesCommon PROXY;

	public ModularPipes() {
		PROXY = DistExecutor.safeRunForDist(() -> ModularPipesClient::new, () -> ModularPipesCommon::new);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		ModularPipesBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesNet.init();
		PROXY.init();
	}

	private void setup(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(PipeModule.class, new Capability.IStorage<PipeModule>() {
			@Nullable
			@Override
			public Tag writeNBT(Capability<PipeModule> capability, PipeModule object, Direction arg) {
				return null;
			}

			@Override
			public void readNBT(Capability<PipeModule> capability, PipeModule object, Direction arg, Tag arg2) {
			}
		}, () -> null);
	}
}