package com.latmod.modularpipes;

import com.latmod.modularpipes.api_impl.PipeNetwork;
import com.latmod.modularpipes.item.ItemBlockVariants;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.MessageUpdateItems;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
        register(new ItemBlockVariants(ModularPipesItems.PIPE_BASIC));
        register(new ItemBlockVariants(ModularPipesItems.PIPE_MODULAR));
        //register(new ItemBlock(ModularPipesItems.CONTROLLER));
        register(ModularPipesItems.MODULE);

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            register(m);
        }

        GameRegistry.registerTileEntity(TileModularPipe.class, ModularPipes.MOD_ID + ":pipe_modular");
        //GameRegistry.registerTileEntity(TileController.class, ModularPipes.MOD_ID + ":controller");

        ModularPipesCaps.init();
        ModularPipes.NET.registerMessage(MessageUpdateItems.class, MessageUpdateItems.class, 1, Side.CLIENT);
    }

    private void register(Item item)
    {
        if(item instanceof ItemBlock)
        {
            ItemBlock block = (ItemBlock) item;
            GameRegistry.register(block.getBlock());
            GameRegistry.register(block.setRegistryName(block.getBlock().getRegistryName()));
        }
        else
        {
            GameRegistry.register(item);
        }
    }

    public void onInit()
    {
        MinecraftForge.EVENT_BUS.register(ModularPipesEventHandler.class);
        Object basicPipe = new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 0);

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 8, 0), " R ", "SGS", " R ", 'S', "cobblestone", 'G', "paneGlass", 'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModularPipesItems.PIPE_BASIC, 1, 1), "NGN", "GPG", "NGN", 'N', "nuggetGold", 'G', "dustGlowstone", 'P', basicPipe));

        addCircularOreRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, 0), "dustRedstone", basicPipe);

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
            addCircularOreRecipe(new ItemStack(ModularPipesItems.PIPE_MODULAR, 8, meta + 1), items[meta], new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, meta));
        }

        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModularPipesItems.CONTROLLER), "IPI", "PCP", "IPI", 'C', Items.COMPARATOR, 'I', "ingotIron", 'P', new ItemStack(ModularPipesItems.PIPE_MODULAR, 1, 4)));

        addCircularOreRecipe(new ItemStack(ModularPipesItems.MODULE, 8), basicPipe, "ingotIron");
    }

    public PipeNetwork getClientNetwork()
    {
        return null;
    }

    private static void addCircularOreRecipe(ItemStack out, Object center, Object around)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(out, "AAA", "ACA", "AAA", 'C', center, 'A', around));
    }
}