package com.latmod.modularpipes;

import com.latmod.modularpipes.api.IPipeController;
import com.latmod.modularpipes.api.IPipeNetworkTile;
import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.item.ItemBlockPipe;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.net.MessageUpdateItems;
import com.latmod.modularpipes.tile.TilePipe;
import com.latmod.modularpipes.util.EmptyStorage;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
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
            return new ItemStack(ModularPipesItems.PIPE);
        }
    };

    @CapabilityInject(Module.class)
    public static Capability<Module> CAP_MODULE;

    @CapabilityInject(IPipeNetworkTile.class)
    public static Capability<Module> CAP_PIPE_NET_TILE;

    @CapabilityInject(IPipeController.class)
    public static Capability<Module> CAP_PIPE_CONTROLLER;

    public void preInit()
    {
        GameRegistry.register(ModularPipesItems.PIPE);
        GameRegistry.register(new ItemBlockPipe(ModularPipesItems.PIPE).setRegistryName(ModularPipesItems.PIPE.getRegistryName()));

        GameRegistry.register(ModularPipesItems.CONTROLLER);
        GameRegistry.register(new ItemBlock(ModularPipesItems.CONTROLLER).setRegistryName(ModularPipesItems.CONTROLLER.getRegistryName()));

        GameRegistry.register(ModularPipesItems.MODULE);

        for(ItemModule m : ModularPipesItems.MODULE_LIST)
        {
            GameRegistry.register(m);
        }

        GameRegistry.registerTileEntity(TilePipe.class, ModularPipes.MOD_ID + ":pipe");

        CapabilityManager.INSTANCE.register(Module.class, EmptyStorage.getInstance(), () -> null);
        CapabilityManager.INSTANCE.register(IPipeNetworkTile.class, EmptyStorage.getInstance(), () -> null);
        CapabilityManager.INSTANCE.register(IPipeController.class, EmptyStorage.getInstance(), () -> null);

        MinecraftForge.EVENT_BUS.register(ModularPipesEventHandler.class);

        ModularPipes.NET.registerMessage(MessageUpdateItems.class, MessageUpdateItems.class, 1, Side.CLIENT);
    }

    public void postInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModularPipesItems.PIPE, 8, 0), " R ", "SGS", " R ", 'S', "cobblestone", 'G', "paneGlass", 'R', "dustRedstone"));

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
            addCircularOreRecipe(new ItemStack(ModularPipesItems.PIPE, 8, meta + 1), items[meta], new ItemStack(ModularPipesItems.PIPE, 1, meta));
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModularPipesItems.CONTROLLER), "IPI", "PCP", "IPI", 'C', Items.COMPARATOR, 'I', "ingotIron", 'P', new ItemStack(ModularPipesItems.PIPE, 1, 4)));
    }

    private static void addCircularOreRecipe(ItemStack out, Object center, Object around)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(out, "AAA", "ACA", "AAA", 'C', center, 'A', around));
    }
}