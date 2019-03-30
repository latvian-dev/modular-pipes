package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.ModularPipesUtils;
import com.latmod.mods.modularpipes.block.EnumMK;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		quads.addAll(parent.modelCallback.get(parent.modelPipe.modelBase, ModelRotation.X0_Y0, new AbstractMap.SimpleEntry<>("material", new ResourceLocation(parent.particle.getIconName()))));
		quads.addAll(parent.glassBase.get(0));

		if (mk != null)
		{
			quads.addAll(parent.overlay.get(mk.ordinal()));
		}

		quads = Collections.unmodifiableList(ModularPipesUtils.optimize(quads));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
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
		return ItemOverrideList.NONE;
	}

	@Override
	public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	{
		return ModelPipe.handleModelPerspective(this, cameraTransformType);
	}
}