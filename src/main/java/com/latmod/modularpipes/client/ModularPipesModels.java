package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * @author LatvianModder
 */
public class ModularPipesModels implements ICustomModelLoader
{
    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        return modelLocation instanceof ModelResourceLocation && modelLocation.getResourceDomain().equals(ModularPipes.MOD_ID) && modelLocation.getResourcePath().equals("pipe");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        return new PipeModel(((ModelResourceLocation) modelLocation).getVariant());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
    }
}