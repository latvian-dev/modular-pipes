package dev.latvian.mods.modularpipes.client;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.ArrayList;
import java.util.Collection;
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
		BakedModel getModel(UnbakedModel model, ModelState rotation, boolean uvlock);

		default List<BakedQuad> get(UnbakedModel model, ModelState rotation, boolean uvlock) {
			return getModel(model, rotation, uvlock).getQuads(null, null, new Random());
		}
	}

	public PipeModelType modelType;
	public boolean pipeGlass;
	public Material material;
	public List<UnbakedModel> models = new ArrayList<>(7);
	public UnbakedModel modelBase, modelConnection, modelVertical;
	public UnbakedModel modelModule, modelLight;
	public UnbakedModel modelGlassBase, modelGlassConnection, modelGlassVertical;
	public UnbakedModel modelOverlay;

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<Material> materials = Sets.newHashSet();
		materials.add(material);

		for (UnbakedModel m : models) {
			materials.addAll(m.getMaterials(modelGetter, missingTextureErrors));
		}

		return materials;
	}

	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
		return new BakedPipeModel(this, spriteGetter.apply(material), new ModelCallbackImpl(bakery, spriteGetter, modelTransform, modelLocation));
	}

	private static class ModelCallbackImpl implements ModelCallback {
		private final ModelBakery bakery;
		private final Function<Material, TextureAtlasSprite> spriteGetter;
		private final ModelState modelState;
		private final ResourceLocation modelLocation;

		public ModelCallbackImpl(ModelBakery b, Function<Material, TextureAtlasSprite> s, ModelState ms, ResourceLocation ml) {
			bakery = b;
			spriteGetter = s;
			modelState = ms;
			modelLocation = ml;
		}

		@Override
		public BakedModel getModel(UnbakedModel model, ModelState rotation, boolean uvlock) {
			return model.bake(bakery, spriteGetter, new ModelTransformComposition(modelState, rotation, uvlock), modelLocation);
		}
	}
}