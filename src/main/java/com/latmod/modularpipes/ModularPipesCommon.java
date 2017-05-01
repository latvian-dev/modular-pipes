package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.RecipeUtils;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.ModularPipesNet;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
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
        //LMUtils.register(ModularPipesItems.CONTROLLER);

        LMUtils.register(ModularPipesItems.MODULE);
        LMUtils.register(ModularPipesItems.DEBUG);

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            LMUtils.register(m);
        }

        GameRegistry.registerTileEntity(TileModularPipe.class, ModularPipes.MOD_ID + ":pipe_modular");
        //GameRegistry.registerTileEntity(TileController.class, ModularPipes.MOD_ID + ":controller");

        ModularPipesCaps.init();
        ModularPipesNet.init();
    }

    public void onInit()
    {
        MinecraftForge.EVENT_BUS.register(ModularPipesEventHandler.class);
        Object basicPipe = new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 0);

        RecipeUtils.addRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 8, 0), " R ", "SGS", " R ", 'S', "cobblestone", 'G', "paneGlass", 'R', "dustRedstone");
        RecipeUtils.addRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 1), "NGN", "GPG", "NGN", 'N', "nuggetGold", 'G', "dustGlowstone", 'P', basicPipe);

        RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, 0), "dustRedstone", basicPipe);

        Object[] items = {
                "ingotIron",
                "ingotGold",
                "gemQuartz",
                "gemLapis",
                "gemEmerald",
                "enderpearl",
                Items.NETHER_STAR
        };

        for(int meta = 0; meta < 7; meta++)
        {
            RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, meta + 1), items[meta], new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, meta));
        }

        //RecipeUtils.addRecipe(new ItemStack(ModularPipesItems.CONTROLLER), "IPI", "PCP", "IPI", 'C', Items.COMPARATOR, 'I', "ingotIron", 'P', new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, 4));

        RecipeUtils.addCircularRecipe(new ItemStack(ModularPipesItems.MODULE, 8), basicPipe, "ingotIron");
    }
}