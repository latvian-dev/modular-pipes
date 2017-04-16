package com.latmod.modularpipes.tile;

import net.minecraft.tileentity.TileEntity;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class TileController extends TileEntity
{
    public boolean error = new Random().nextBoolean();
}