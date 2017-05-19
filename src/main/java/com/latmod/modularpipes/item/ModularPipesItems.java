package com.latmod.modularpipes.item;

import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.block.BlockPipeBasic;
import com.latmod.modularpipes.block.BlockPipeBasicNode;
import com.latmod.modularpipes.item.module.ModuleBlockItems;
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
    public static final List<Item> MODULE_LIST = new ArrayList<>();
    public static final Item DEBUG = new ItemDebug("debug");

    static
    {
        MODULE_LIST.add(new ItemModule("extract", new ModuleExtract()));
        MODULE_LIST.add(new ItemModule("rightclick_extract", new ModuleRightClickExtract()));
        MODULE_LIST.add(new ItemModule("block_items", new ModuleBlockItems()));
    }
}