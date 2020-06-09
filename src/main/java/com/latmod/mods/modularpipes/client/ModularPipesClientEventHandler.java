package com.latmod.mods.modularpipes.client;

import com.google.common.collect.ImmutableMap;
import com.latmod.mods.modularpipes.ModularPipes;
import com.latmod.mods.modularpipes.ModularPipesUtils;
import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.ModularPipesBlocks;
import com.latmod.mods.modularpipes.item.ModularPipesItems;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TileTank;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Dist.CLIENT)
public class ModularPipesClientEventHandler
{
	public static ModelResourceLocation[] locs = new ModelResourceLocation[] {
			new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe_transport", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe_modular_mk1", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe_modular_mk2", ""),
			new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe_modular_mk3", ""),
	};

	private static void addModel(Item item, String variant)
	{
		//		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	private static void addPipeModel(Block block, Item item)
	{
		//		ModelLoader.setCustomStateMapper(block, ModelPipeLoader.INSTANCE);
		//		ModelLoader.setCustomModelResourceLocation(item, 0, ModelPipeLoader.ID);
	}

	public static void textureStitch(TextureStitchEvent.Pre event)
	{
		if (event.getMap().getBasePath().equals("textures"))
		{
			ModelPipeLoader.INSTANCE.model.textures.forEach(event::addSprite);
		}
	}

	public static void modelBake(ModelBakeEvent event)
	{
		ModelPipeBaked bm = new ModelPipeBaked(ModelPipeLoader.INSTANCE.model, Minecraft.getInstance().getTextureMap().getAtlasSprite("minecraft:block/gray_concrete"), (id, rotation, uvlock, retextures) ->
		{
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures)
			{
				builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			BlockModel model = null;//.smoothLighting(false);
			IResource iresource;
			try
			{
				iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(id.getNamespace(), "models/" + id.getPath() + ".json"));
				model = BlockModel.deserialize(IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8));
				builder.build().forEach(model.textures::put);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			IBakedModel bakedModel = model.bake(event.getModelLoader(), Minecraft.getInstance().getTextureMap()::getSprite, rotation, DefaultVertexFormats.BLOCK);
			return ModularPipesUtils.optimize(bakedModel.getQuads(Blocks.AIR.getDefaultState(), null, new Random(), new ModelDataMap.Builder().build()));
		});
		for (ModelResourceLocation loc : locs)
		{
			event.getModelRegistry().put(loc, bm);
			String s = loc.getPath();
			EnumMK mk = s.endsWith("1") ? EnumMK.MK1 : s.endsWith("2") ? EnumMK.MK2 : s.endsWith("3") ? EnumMK.MK3 : null;
			event.getModelRegistry().put(new ModelResourceLocation(loc, "inventory"), new ModelPipeBakedItem(bm, mk));
		}
	}

	public static void registerModels(ModelRegistryEvent event)
	{
		addPipeModel(ModularPipesBlocks.PIPE_TRANSPORT, ModularPipesItems.PIPE_TRANSPORT);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK1, ModularPipesItems.PIPE_MODULAR_MK1);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK2, ModularPipesItems.PIPE_MODULAR_MK2);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK3, ModularPipesItems.PIPE_MODULAR_MK3);
		addModel(ModularPipesItems.TANK, "inventory");
		addModel(ModularPipesItems.MODULAR_STORAGE, "normal");
		addModel(ModularPipesItems.MODULAR_TANK, "normal");

		addModel(ModularPipesItems.PAINTER, "inventory");
		addModel(ModularPipesItems.MODULE, "inventory");
		addModel(ModularPipesItems.INPUT_PART, "inventory");
		addModel(ModularPipesItems.OUTPUT_PART, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_STORAGE, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_BASE, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_INSERT, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_STORAGE, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_BASE, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_INSERT, "inventory");
		addModel(ModularPipesItems.MODULE_CRAFTING, "inventory");
		addModel(ModularPipesItems.MODULE_ENERGY_INPUT, "inventory");
		addModel(ModularPipesItems.MODULE_ENERGY_OUTPUT, "inventory");

		ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new RenderTank());
	}

	@SubscribeEvent
	public static void tickClientWorld(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getInstance();

		if (event.phase == TickEvent.Phase.END && mc.world != null && !mc.isGamePaused())
		{
			PipeNetwork.get(mc.world).tick();
		}
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event)
	{
		PipeNetwork.get(Minecraft.getInstance().world).render(event.getPartialTicks());
	}
}