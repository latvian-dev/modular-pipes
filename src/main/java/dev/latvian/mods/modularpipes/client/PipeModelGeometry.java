package dev.latvian.mods.modularpipes.client;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.ModularPipesUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class PipeModelGeometry implements IModelGeometry<PipeModelGeometry> {
	public static final BlockModelRotation[] FACE_ROTATIONS = {
			BlockModelRotation.X0_Y0,
			BlockModelRotation.X180_Y0,
			BlockModelRotation.X90_Y180,
			BlockModelRotation.X90_Y0,
			BlockModelRotation.X90_Y90,
			BlockModelRotation.X90_Y270
	};

	public interface ModelCallback {
		List<BakedQuad> get(ResourceLocation id, BlockModelRotation rotation, boolean uvlock);

		default List<BakedQuad> get(ResourceLocation id, BlockModelRotation rotation) {
			return get(id, rotation, true);
		}
	}

	public final ResourceLocation modelBase, modelConnection, modelVertical;
	public final ResourceLocation modelOverlay, modelModule;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;

	public PipeModelGeometry() {
		modelBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/base");
		modelConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/connection");
		modelVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/vertical");
		modelOverlay = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay");
		modelModule = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/module");
		modelGlassBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_base");
		modelGlassConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_connection");
		modelGlassVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_vertical");
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Collection<Material> textures = new HashSet<>();
		textures.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ModularPipes.MOD_ID, "block/glass")));
		textures.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/module")));
		textures.add(owner.resolveTexture("material"));
		return textures;
	}

	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
		TextureAtlasSprite base = spriteGetter.apply(owner.resolveTexture("material"));
		return new BakedPipeModel(this, base, new ModelCallbackImpl(bakery, spriteGetter, base));
	}

	private static class ModelCallbackImpl implements ModelCallback, Function<Material, TextureAtlasSprite> {
		private final ModelBakery bakery;
		private final Function<Material, TextureAtlasSprite> spriteGetter;
		private final TextureAtlasSprite base;

		public ModelCallbackImpl(ModelBakery b, Function<Material, TextureAtlasSprite> s, TextureAtlasSprite ba) {
			bakery = b;
			spriteGetter = s;
			base = ba;
		}

		@Override
		public List<BakedQuad> get(ResourceLocation id, BlockModelRotation rotation, boolean uvlock) {
			/*
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures) {
				builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			bakery.getModel()

			IModel model = bakery.getModel(id).retexture(builder.build());//.smoothLighting(false);
			BakedModel bakedModel = model.bake(bakery, spriteGetter, sprite, format);
			return ModularPipesUtils.optimize(bakedModel.getQuads(null, null, new Random()));
			 */

			return ModularPipesUtils.optimize(bakery.getBakedModel(id, rotation, this).getQuads(null, null, new Random()));
		}

		@Override
		public TextureAtlasSprite apply(Material material) {
			// This is terrible implementation but its the best I can find for now
			if (material.texture().equals(MissingTextureAtlasSprite.getLocation())) {
				return base;
			}

			return spriteGetter.apply(material);
		}
	}
}