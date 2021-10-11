package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Dist.CLIENT)
public class ModularPipesClientEventHandler {
	public static ModelResourceLocation[] locs = new ModelResourceLocation[]{
			new ModelResourceLocation(ModularPipes.MOD_ID + ":transport_pipe", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":modular_pipe_mk1", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":modular_pipe_mk2", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":modular_pipe_mk3", ""),
	};

	public static void textureStitch(TextureStitchEvent.Pre event) {
		if (event.getMap().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			// PipeModelLoader.INSTANCE.model.textures.forEach(m -> event.addSprite(m.texture()));
		}
	}

	public static void modelBake(ModelBakeEvent event) {
		//BakedPipeModel bm = new BakedPipeModel(PipeModelLoader.IN/STANCE.model, Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation("minecraft:block/gray_concrete")), (id, rotation, uvlock, retextures) ->
		{
			/*
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures) {
				builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			BlockModel model = null;//.smoothLighting(false);
			Resource iresource;
			try {
				iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(id.getNamespace(), "models/" + id.getPath() + ".json"));
				model = BlockModel.fromString(IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8));
				builder.build().forEach(model.textures::put);
			} catch (Exception e) {
				e.printStackTrace();
			}

			BakedModel bakedModel = model.bake(event.getModelLoader(), Minecraft.getInstance().getTextureMap()::getSprite, rotation, DefaultVertexFormat.BLOCK);
			return ModularPipesUtils.optimize(bakedModel.getQuads(null, null, new Random(), new ModelDataMap.Builder().build()));
			 */
			// return ModularPipesUtils.optimize(bakery.getBakedModel(id, modelTransform, spriteGetter).getQuads(null, null, new Random()));
			//return Collections.emptyList();
		}//);

		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":block/transport_pipe", ""), bm);
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":block/modular_pipe_mk1", ""), bm);
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":block/modular_pipe_mk2", ""), bm);
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":block/modular_pipe_mk3", ""), bm);

		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":item/transport_pipe", "inventory"), new BakedPipeItemModel(bm, null));
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":item/modular_pipe_mk1", "inventory"), new BakedPipeItemModel(bm, EnumMK.MK1));
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":item/modular_pipe_mk2", "inventory"), new BakedPipeItemModel(bm, EnumMK.MK2));
		//event.getModelRegistry().put(new ModelResourceLocation(ModularPipes.MOD_ID + ":item/modular_pipe_mk3", "inventory"), new BakedPipeItemModel(bm, EnumMK.MK3));
	}

	public static void registerModels(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(ModularPipes.MOD_ID + ":pipe"), PipeModelLoader.INSTANCE);
	}

	@SubscribeEvent
	public static void tickClientWorld(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if (event.phase == TickEvent.Phase.END && mc.level != null && !mc.isPaused()) {
			PipeNetwork.get(mc.level).tick();
		}
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		PipeNetwork.get(Minecraft.getInstance().level).render(event);
	}
}