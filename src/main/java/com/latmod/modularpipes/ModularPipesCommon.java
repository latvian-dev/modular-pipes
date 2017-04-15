package com.latmod.modularpipes;

import com.latmod.modularpipes.item.ItemBlockPipe;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.tile.TilePipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon
{
    public static final CreativeTabs TAB = new CreativeTabs(ModularPipes.MOD_ID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModularPipesItems.PIPE);
        }
    };

    public void preInit()
    {
        GameRegistry.register(ModularPipesItems.PIPE);
        GameRegistry.register(new ItemBlockPipe(ModularPipesItems.PIPE).setRegistryName(ModularPipesItems.PIPE.getRegistryName()));

        GameRegistry.register(ModularPipesItems.MODULE);

        GameRegistry.registerTileEntity(TilePipe.class, ModularPipes.MOD_ID + ":pipe");
    }

    public void postInit()
    {
    }
}