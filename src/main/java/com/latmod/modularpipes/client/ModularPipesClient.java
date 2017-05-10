package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.block.BlockBasicPipe;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.block.Block;
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
    public void onPreInit()
    {
        super.onPreInit();

        for(BlockBasicPipe.Variant v : BlockBasicPipe.Variant.VALUES)
        {
            registerModel(ModularPipesItems.PIPE_BASIC, v.ordinal(), "model=none,variant=" + v.getName());
        }

        for(int meta : BlockModularPipe.TIER.getAllowedValues())
        {
            registerModel(ModularPipesItems.PIPE_MODULAR, meta, "con_d=0,con_e=0,con_n=0,con_s=0,con_u=0,con_w=0,tier=" + meta);
        }

        //registerModel(ModularPipesItems.CONTROLLER, 0, "error=false");
        registerModel(ModularPipesItems.MODULE, 0, "inventory");
        registerModel(ModularPipesItems.DEBUG, 0, "inventory");

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            ModelLoader.setCustomModelResourceLocation(m, 0, new ModelResourceLocation(m.getRegistryName().toString().replace("module_", "module/"), "inventory"));
        }
    }

    private void registerModel(Block block, int meta, String variant)
    {
        registerModel(Item.getItemFromBlock(block), meta, variant);
    }

    private void registerModel(Item item, int meta, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    @Override
    public void onInit()
    {
        super.onInit();

        MinecraftForge.EVENT_BUS.register(ModularPipesClientEventHandler.class);
    }

    @Override
    public PipeNetwork getClientNetwork()
    {
        return ClientPipeNetwork.get();
    }
}