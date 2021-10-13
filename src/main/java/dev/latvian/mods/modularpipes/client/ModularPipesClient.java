package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.ModularPipesCommon;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import javax.annotation.Nullable;

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
	public void spawnParticle(BlockPos pos, @Nullable Direction facing, int type) {
		Minecraft mc = Minecraft.getInstance();
		double x = pos.getX() + 0.5D + (facing == null ? 0D : facing.getStepX() * 0.3D);
		double y = pos.getY() + 0.5D + (facing == null ? 0D : facing.getStepY() * 0.3D);
		double z = pos.getZ() + 0.5D + (facing == null ? 0D : facing.getStepZ() * 0.3D);

		//		if (type == EXPLOSION)
		//		{
		//			mc.particles.addEffect(explosionFactory.createParticle(0, mc.world, x, y, z, 0D, 0D, 0D));
		//		}
		//		else if (type == SPARK)
		//		{
		//			mc.particles.addEffect(redstoneFactory.createParticle(0, mc.world, x, y, z, 1D, 0.8D, 0D));
		//		}
	}

	@Override
	public int getPipeLightValue(BlockGetter level) {
		if (level instanceof LevelReader && !((LevelReader) level).isClientSide()) {
			return 0;
		}

		return MinecraftForgeClient.getRenderLayer() == RenderType.cutout() ? 15 : 0;
	}

	public void registerModels(ModelRegistryEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.TRANSPORT_PIPE.get(), r -> r == RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.FAST_TRANSPORT_PIPE.get(), r -> r == RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK1.get(), r -> r == RenderType.cutoutMipped() || r == RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK2.get(), r -> r == RenderType.cutoutMipped() || r == RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModularPipesBlocks.MODULAR_PIPE_MK3.get(), r -> r == RenderType.cutoutMipped() || r == RenderType.cutout());
		ModelLoaderRegistry.registerLoader(new ResourceLocation(ModularPipes.MOD_ID + ":pipe"), PipeModelLoader.INSTANCE);
	}

	public void tickClientWorld(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if (event.phase == TickEvent.Phase.END && mc.level != null && !mc.isPaused()) {
			PipeNetwork.get(mc.level).tick();
		}
	}

	public void renderWorld(RenderWorldLastEvent event) {
		PipeNetwork.get(Minecraft.getInstance().level).render(event);
	}
}