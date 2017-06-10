package com.latmod.modularpipes.item;

import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.block.BlockPipeBasic;
import com.latmod.modularpipes.block.BlockPipeBasicNode;
import com.latmod.modularpipes.data.Module;
import com.latmod.modularpipes.item.module.ModuleCrafting;
import com.latmod.modularpipes.item.module.ModuleExtract;
import com.latmod.modularpipes.item.module.ModuleRightClickExtract;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesItems
{
    public static final Block PIPE_BASIC = new BlockPipeBasic("pipe_basic", MapColor.GRAY);
    public static final Block PIPE_MODULAR = new BlockModularPipe("pipe_modular");
    public static final Block PIPE_NODE = new BlockPipeBasicNode("pipe_node");

    public static final Item MODULE = new ItemMPBase("module");
    public static final Item DEBUG = new ItemDebug("debug");

    public static class Modules
    {
        public static final List<Item> LIST = new ArrayList<>();

        public static final ItemModule EXTRACT = add("extract", new ModuleExtract());
        public static final ItemModule RIGHTCLICK_EXTRACT = add("rightclick_extract", new ModuleRightClickExtract());
        public static final ItemModule CRAFTING = add("crafting", new ModuleCrafting());

        private static ItemModule add(String id, Module module)
        {
            ItemModule m = new ItemModule(id, module);
            LIST.add(m);
            return m;
        }
    }
}