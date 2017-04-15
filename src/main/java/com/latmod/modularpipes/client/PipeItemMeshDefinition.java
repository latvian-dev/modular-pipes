package com.latmod.modularpipes.client;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeItemMeshDefinition implements ItemMeshDefinition
{
    private final List<ModelResourceLocation> pipeVariants;

    public PipeItemMeshDefinition(List<ModelResourceLocation> v)
    {
        pipeVariants = v;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        return pipeVariants.get(stack.getMetadata());
    }
}