package com.latmod.modularpipes.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.EnumMK;
import com.latmod.modularpipes.block.PipeSkin;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ModelPipe extends DefaultStateMapper implements IModel
{
	public static final ModelRotation[] FACE_ROTATIONS = {
			ModelRotation.X0_Y0,
			ModelRotation.X180_Y0,
			ModelRotation.X90_Y180,
			ModelRotation.X90_Y0,
			ModelRotation.X90_Y90,
			ModelRotation.X90_Y270
	};

	private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> TRANSFORM_MAP;

	private static TRSRTransformation get(float ty, float ax, float ay, float s)
	{
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new javax.vecmath.Vector3f(0F, ty / 16F, 0F), TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, 0F)), new javax.vecmath.Vector3f(s, s, s), null));
	}

	static
	{
		TRSRTransformation thirdperson = get(2.5F, 75, 45, 0.375F);
		TRSRTransformation flipX = new TRSRTransformation(null, null, new javax.vecmath.Vector3f(-1, 1, 1), null);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, get(0, 30, 225, 0.625F));
		builder.put(ItemCameraTransforms.TransformType.GROUND, get(3, 0, 0, 0.25F));
		builder.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0.5F));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(thirdperson)).compose(flipX)));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 45, 0.4F));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 225, 0.4F));
		TRANSFORM_MAP = Maps.immutableEnumMap(builder.build());
	}

	public static org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handleModelPerspective(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType)
	{
		return PerspectiveMapWrapper.handlePerspective(model, TRANSFORM_MAP, cameraTransformType);
	}

	public final Collection<ResourceLocation> models;
	public final ResourceLocation modelBase, modelConnection, modelVertical;
	public final ResourceLocation modelOverlay;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;

	public final Collection<ResourceLocation> textures;
	public final ResourceLocation textureParticle;
	public final ResourceLocation[] overlayTextures;

	public ModelPipe()
	{
		Collection<ResourceLocation> models0 = new HashSet<>();
		models0.add(modelBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/base"));
		models0.add(modelConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/connection"));
		models0.add(modelVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/vertical"));
		models0.add(modelOverlay = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay"));
		models0.add(modelGlassBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_base"));
		models0.add(modelGlassConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_connection"));
		models0.add(modelGlassVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_vertical"));

		models = Collections.unmodifiableCollection(models0);
		textures = new HashSet<>();

		overlayTextures = new ResourceLocation[EnumMK.VALUES.length];

		for (int i = 0; i < EnumMK.VALUES.length; i++)
		{
			textures.add(overlayTextures[i] = new ResourceLocation("modularpipes:blocks/pipe/overlay/" + EnumMK.VALUES[i].getName()));
		}

		textures.add(textureParticle = new ResourceLocation("minecraft:blocks/concrete_gray"));

		for (PipeSkin skin : PipeSkin.MAP.values())
		{
			textures.add(skin.texture);
		}
	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return models;
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> tex)
	{
		return new ModelPipeBaked(this, tex.apply(textureParticle), (id, rotation, retextures) ->
		{
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures)
			{
				builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			IModel model = ModelLoaderRegistry.getModelOrMissing(id).uvlock(true).retexture(builder.build());//.smoothLighting(false);
			IBakedModel bakedModel = model.bake(rotation, format, tex);
			return Arrays.asList(bakedModel.getQuads(null, null, 0L).toArray(new BakedQuad[0]));
		});
	}

	public interface ModelCallback
	{
		List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, Map.Entry... retextures);
	}
}