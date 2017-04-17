package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.api.IPipeController;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class TileController extends TilePipeNetBase implements IPipeController
{
    private boolean error = new Random().nextBoolean();

    public TileController()
    {
        this(0);
    }

    public TileController(int dim)
    {
        super(dim);
    }

    @Override
    public boolean hasError()
    {
        return error;
    }

    @Override
    public void setPipeController(IPipeController c)
    {
        if(c != this)
        {
            error = true;
        }
    }

    @Override
    public IPipeController getPipeController()
    {
        return this;
    }
}