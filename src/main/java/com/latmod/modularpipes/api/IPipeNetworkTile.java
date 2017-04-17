package com.latmod.modularpipes.api;

/**
 * @author LatvianModder
 */
public interface IPipeNetworkTile
{
    boolean hasError();

    void setPipeController(IPipeController controller);

    IPipeController getPipeController();
}