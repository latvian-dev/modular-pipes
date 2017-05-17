package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
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

        registerModel(ModularPipesItems.PIPE_BASIC, 0, "model=none");
        registerModel(ModularPipesItems.PIPE_SPEED, 0, "model=none");

        for(BlockModularPipe.Tier tier : BlockModularPipe.Tier.VALUES)
        {
            registerModel(ModularPipesItems.PIPE_MODULAR, tier.ordinal(), "con_d=0,con_e=0,con_n=0,con_s=0,con_u=0,con_w=0,tier=" + tier.getName());
        }

        //registerModel(ModularPipesItems.CONTROLLER, 0, "error=false");
        registerModel(ModularPipesItems.MODULE, 0, "inventory");
        registerModel(ModularPipesItems.DEBUG, 0, "inventory");

        for(Item m : ModularPipesItems.MODULE_LIST)
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
    public PipeNetwork getClientNetwork(World world)
    {
        if(ClientPipeNetwork.INSTANCE == null || world != ClientPipeNetwork.INSTANCE.world)
        {
            ClientPipeNetwork.INSTANCE = new ClientPipeNetwork(world);
        }
        return ClientPipeNetwork.INSTANCE;
    }
}