package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.block.entity.ModularPipesBlockEntities;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import dev.latvian.mods.modularpipes.client.ModularPipesClient;
import dev.latvian.mods.modularpipes.item.ModularPipesItems;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModularPipes.MOD_ID)
public class ModularPipes {
	public static final String MOD_ID = "modularpipes";

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModularPipesItems.MODULAR_PIPE_MK1.get());
		}
	};

	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	public static ModularPipesCommon PROXY;

	public ModularPipes() {
		PROXY = DistExecutor.safeRunForDist(() -> ModularPipesClient::new, () -> ModularPipesCommon::new);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(Level.class, this::attachLevelCap);
		MinecraftForge.EVENT_BUS.addListener(this::tickServerWorld);
		ModularPipesBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModularPipesNet.init();
		PROXY.init();
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PipeModule.class);
		event.register(PipeNetwork.class);
	}

	private void attachLevelCap(AttachCapabilitiesEvent<Level> event) {
		event.addCapability(WORLD_CAP_ID, new PipeNetwork(event.getObject()));
	}

	private void tickServerWorld(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			PipeNetwork network = PipeNetwork.get(event.world);

			if (network != null) {
				network.tick();
			}
		}
	}
}