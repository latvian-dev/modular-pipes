package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

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

        for(int m = 0; m < 8; m++)
        {
            registerModel(ModularPipesItems.PIPE_MODULAR, m, ModularPipes.MOD_ID + ":pipe_modular_item#tier=" + (m & 7));
        }

        registerModel(ModularPipesItems.PIPE_NODE, 0, ModularPipesItems.PIPE_BASIC.getRegistryName() + "#model=none");

        registerModel(ModularPipesItems.MODULE, 0, "inventory");
        registerModel(ModularPipesItems.DEBUG, 0, "inventory");

        for(Item m : ModularPipesItems.MODULE_LIST)
        {
            registerModel(m, 0, m.getRegistryName().toString().replace("module_", "module/") + "#inventory");
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileModularPipe.class, new RenderModularPipe());
    }

    private void registerModel(Block block, int meta, String variant)
    {
        registerModel(Item.getItemFromBlock(block), meta, variant);
    }

    private void registerModel(Item item, int meta, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, variant.indexOf('#') != -1 ? new ModelResourceLocation(variant) : new ModelResourceLocation(item.getRegistryName(), variant));
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