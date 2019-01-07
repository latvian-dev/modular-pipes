package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * @author LatvianModder
 */
public class ModelPipeLoader extends DefaultStateMapper implements ICustomModelLoader
{
	public static final ModelPipeLoader INSTANCE = new ModelPipeLoader();
	public static final ModelResourceLocation ID = new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe#normal");

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return ID;
	}

	@Override
	public boolean accepts(ResourceLocation id)
	{
		return ID.getNamespace().equals(id.getNamespace()) && ID.getPath().equals(id.getPath());
	}

	@Override
	public IModel loadModel(ResourceLocation id)
	{
		return new ModelPipe();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
	}
}