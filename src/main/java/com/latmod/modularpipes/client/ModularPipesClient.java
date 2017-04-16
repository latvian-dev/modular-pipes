package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
    @Override
    public void preInit()
    {
        super.preInit();
        /*
        ModelLoader.setCustomStateMapper(ModularPipesItems.PIPE, new StateMap.Builder().ignore(BlockPipe.CON_DOWN, BlockPipe.CON_UP, BlockPipe.CON_NORTH, BlockPipe.CON_SOUTH, BlockPipe.CON_WEST, BlockPipe.CON_EAST).build());
        ModelLoaderRegistry.registerLoader(new ModularPipesModels());
        Item pipeItem = Item.getItemFromBlock(ModularPipesItems.PIPE);

        List<ModelResourceLocation> pipeVariants = new ArrayList<>();

        for(int meta = 0; meta < 8; meta++)
        {
            pipeVariants.add(new ModelResourceLocation(ModularPipesItems.PIPE.getRegistryName(), "tier=" + meta));
        }

        ModelLoader.registerItemVariants(pipeItem, pipeVariants.toArray(new ModelResourceLocation[pipeVariants.size()]));
        ModelLoader.setCustomMeshDefinition(pipeItem, new PipeItemMeshDefinition(pipeVariants));
        */

        Item pipeItem = Item.getItemFromBlock(ModularPipesItems.PIPE);
        for(int meta = 0; meta < 8; meta++)
        {
            ModelLoader.setCustomModelResourceLocation(pipeItem, meta, new ModelResourceLocation(ModularPipesItems.PIPE.getRegistryName(), "con_down=false,con_east=false,con_north=false,con_south=false,con_up=false,con_west=false,model=none,tier=" + meta));
        }

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModularPipesItems.CONTROLLER), 0, new ModelResourceLocation(ModularPipesItems.CONTROLLER.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModularPipesItems.MODULE, 0, new ModelResourceLocation(ModularPipesItems.MODULE.getRegistryName(), "inventory"));

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            ModelLoader.setCustomModelResourceLocation(m, 0, new ModelResourceLocation(m.getRegistryName().toString().replace("module_", "module/"), "inventory"));
        }

        MinecraftForge.EVENT_BUS.register(ModularPipesClientEventHandler.class);
    }
}