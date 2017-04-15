package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.block.BlockPipe;
import com.latmod.modularpipes.block.EnumPipeTier;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
    @Override
    public void preInit()
    {
        super.preInit();
        ModelLoader.setCustomStateMapper(ModularPipesItems.PIPE, new StateMap.Builder().ignore(BlockPipe.OPAQUE, BlockPipe.CON_DOWN, BlockPipe.CON_UP, BlockPipe.CON_NORTH, BlockPipe.CON_SOUTH, BlockPipe.CON_WEST, BlockPipe.CON_EAST).build());
        ModelLoaderRegistry.registerLoader(new ModularPipesModels());
        Item pipeItem = Item.getItemFromBlock(ModularPipesItems.PIPE);

        List<ModelResourceLocation> pipeVariants = new ArrayList<>();

        for(int meta = 0; meta < 16; meta++)
        {
            pipeVariants.add(new ModelResourceLocation(ModularPipesItems.PIPE.getRegistryName(), "tier=" + EnumPipeTier.getFromMeta(meta).getName()));
        }

        ModelLoader.registerItemVariants(pipeItem, pipeVariants.toArray(new ModelResourceLocation[pipeVariants.size()]));
        ModelLoader.setCustomMeshDefinition(pipeItem, new PipeItemMeshDefinition(pipeVariants));

        ModelLoader.setCustomModelResourceLocation(ModularPipesItems.MODULE, 0, new ModelResourceLocation(ModularPipesItems.MODULE.getRegistryName(), "inventory"));
    }
}