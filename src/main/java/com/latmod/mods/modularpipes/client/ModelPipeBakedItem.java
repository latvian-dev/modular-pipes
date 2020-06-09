package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.ModularPipesUtils;
import com.latmod.mods.modularpipes.block.EnumMK;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class ModelPipeBakedItem implements IBakedModel
{
	public final ModelPipeBaked parent;
	private List<BakedQuad> quads;

	public ModelPipeBakedItem(ModelPipeBaked p, @Nullable EnumMK mk)
	{
		parent = p;
		quads = new ArrayList<>();
		quads.addAll(parent.modelCallback.get(parent.modelPipe.modelBase, ModelRotation.X0_Y0, new AbstractMap.SimpleEntry<>("material", parent.particle.getName())));
		quads.addAll(parent.glassBase.get(0));

		if (mk != null)
		{
			quads.addAll(parent.overlay.get(mk.ordinal()));
		}

		quads = Collections.unmodifiableList(ModularPipesUtils.optimize(quads));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
	{
		return quads;
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return true;
	}

	@Override
	public boolean isGui3d()
	{
		return true;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return parent.particle;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.EMPTY;
	}

	@Override
	public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	{
		return ModelPipe.handleModelPerspective(this, cameraTransformType);
	}
}