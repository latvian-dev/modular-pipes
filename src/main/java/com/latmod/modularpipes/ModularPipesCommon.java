package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.item.ODItems;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.RecipeUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
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
            return new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 1);
        }
    };

    public void onPreInit()
    {
        LMUtils.register(ModularPipesItems.PIPE_BASIC);
        LMUtils.register(ModularPipesItems.PIPE_MODULAR);

        LMUtils.register(ModularPipesItems.MODULE);
        LMUtils.register(ModularPipesItems.DEBUG);

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            LMUtils.register(m);
        }

        GameRegistry.registerTileEntity(TileModularPipe.class, ModularPipes.MOD_ID + ":pipe_modular");

        ModularPipesCaps.init();
        ModularPipesNet.init();
    }

    public void onInit()
    {
        MinecraftForge.EVENT_BUS.register(ModularPipesEventHandler.class);
        Object basicPipe = new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 0);

        RecipeUtils.addRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 8, 0), " R ", "SGS", " R ", 'S', ODItems.COBBLE, 'G', ODItems.GLASS_PANE_ANY, 'R', ODItems.REDSTONE);
        RecipeUtils.addRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 1), "NGN", "GPG", "NGN", 'N', ODItems.NUGGET_GOLD, 'G', ODItems.GLOWSTONE, 'P', basicPipe);

        RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, 0), ODItems.REDSTONE, basicPipe);

        Object[] items = {ODItems.IRON, ODItems.GOLD, ODItems.QUARTZ, ODItems.LAPIS, ODItems.EMERALD, ODItems.ENDERPEARL, ODItems.NETHERSTAR};

        for(int meta = 0; meta < 7; meta++)
        {
            RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, meta + 1), items[meta], new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, meta));
        }

        RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.MODULE, 8), basicPipe, ODItems.IRON);
    }

    public PipeNetwork getClientNetwork()
    {
        throw new IllegalStateException();
    }
}