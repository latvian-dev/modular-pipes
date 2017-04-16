package com.latmod.modularpipes.api;

/**
 * @author LatvianModder
 */
public interface IPipeController extends IPipeNetworkTile
{
    @Override
    default IPipeController getPipeController()
    {
        return this;
    }
}