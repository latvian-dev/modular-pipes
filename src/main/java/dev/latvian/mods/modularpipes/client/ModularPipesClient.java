package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.util.PipeItem;
import dev.latvian.mods.modularpipes.util.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon {
	@Override
	public void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerModels);
		MinecraftForge.EVENT_BUS.addListener(this::tickClientWorld);
		MinecraftForge.EVENT_BUS.addListener(this::renderWorld);
	}

	@Override
	public void spawnParticle(double x, double y, double z, int type) {
		if (type >= 0 && type < PipeParticle.VALUES.length) {
			PipeParticle.VALUES[type].factory.create(Minecraft.getInstance().level, x, y, z);
		}
	}

	@Override
	public int getPipeLightValue(BlockGetter level) {
		if (level instanceof LevelReader && !((LevelReader) level).isClientSide()) {
			return 0;
		}

		return MinecraftForgeClient.getRenderLayer() == RenderType.cutoutMipped() ? 15 : 0;
	}

	@Override
	public void updatePipeItem(PipeItem item) {
		PipeNetwork network = PipeNetwork.get(Minecraft.getInstance().level);

		if (network instanceof ClientPipeNetwork) {
			item.network = network;
			PipeItem prev = network.pipeItems.put(item.id, item);

			if (prev != null) {
				prev.ttl = 0;
			}
		}
	}

	@Override
	public void removePipeItem(long id) {
		PipeNetwork network = PipeNetwork.get(Minecraft.getInstance().level);
		PipeItem item = network instanceof ClientPipeNetwork ? network.pipeItems.get(id) : null;

		if (item != null) {
			item.ttl = 0;
		}
	}

	public void registerModels(ModelRegistryEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.TRANSPORT_PIPE.get(), r -> r == RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK1.get(), r -> r == RenderType.cutout() || r == RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK2.get(), r -> r == RenderType.cutout() || r == RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK3.get(), r -> r == RenderType.cutout() || r == RenderType.cutoutMipped());
		ModelLoaderRegistry.registerLoader(new ResourceLocation(ModularPipes.MOD_ID + ":pipe"), PipeModelLoader.INSTANCE);
	}

	public void tickClientWorld(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if (event.phase == TickEvent.Phase.END && mc.level != null && !mc.isPaused()) {
			PipeNetwork network = PipeNetwork.get(mc.level);

			if (network instanceof ClientPipeNetwork) {
				network.tick();
			}
		}
	}

	public void renderWorld(RenderWorldLastEvent event) {
		PipeNetwork network = PipeNetwork.get(Minecraft.getInstance().level);

		if (network instanceof ClientPipeNetwork) {
			((ClientPipeNetwork) network).render(event);
		}
	}
}