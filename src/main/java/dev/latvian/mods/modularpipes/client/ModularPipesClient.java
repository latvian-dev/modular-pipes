package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipesCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon {
	//	private LargeExplosionParticle.Factory explosionFactory = new LargeExplosionParticle.Factory();
	//	private RedstoneParticle.Factory redstoneFactory = new RedstoneParticle.Factory();

	public ModularPipesClient() {
		ModelLoaderRegistry.registerLoader(ModelPipeLoader.INSTANCE);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ModularPipesClientEventHandler::textureStitch);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ModularPipesClientEventHandler::modelBake);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ModularPipesClientEventHandler::registerModels);
//		ScreenManager.registerFactory(ModularPipesContainers.PAINTER, GuiPainter::new);
//		ScreenManager.registerFactory(ModularPipesContainers.PIPE_MODULAR, GuiPipeModular::new);
	}

	@Override
	public void spawnParticle(BlockPos pos, @Nullable Direction facing, int type) {
		Minecraft mc = Minecraft.getInstance();
		double x = pos.getX() + 0.5D + (facing == null ? 0D : facing.getXOffset() * 0.3D);
		double y = pos.getY() + 0.5D + (facing == null ? 0D : facing.getYOffset() * 0.3D);
		double z = pos.getZ() + 0.5D + (facing == null ? 0D : facing.getZOffset() * 0.3D);

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
	public int getPipeLightValue(IBlockReader world) {
		if (world instanceof World) {
			if (!((World) world).isRemote) {
				return 0;
			}
		}

		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT ? 15 : 0;
	}
}