package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.ModularPipes;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class ModelPipeLoader implements ICustomModelLoader
{
	public static final ModelPipeLoader INSTANCE = new ModelPipeLoader();
	public static final ModelResourceLocation ID = new ModelResourceLocation(ModularPipes.MOD_ID + ":pipe");
	public ModelPipe model = new ModelPipe();

	@Override
	public boolean accepts(ResourceLocation id)
	{
		return ID.getNamespace().equals(id.getNamespace()) && ID.getPath().equals(id.getPath());
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation id)
	{
		return model;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		model = new ModelPipe();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
	{
		if (resourcePredicate.test(VanillaResourceType.MODELS))
		{
			model = new ModelPipe();
		}
	}
}