package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.EmptyCapStorage;
import com.latmod.modularpipes.data.IPipeNetworkTile;
import com.latmod.modularpipes.data.Module;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * @author LatvianModder
 */
public class ModularPipesCaps
{
    @CapabilityInject(Module.class)
    public static Capability<Module> MODULE;

    @CapabilityInject(IPipeNetworkTile.class)
    public static Capability<IPipeNetworkTile> PIPE_NET_TILE;

    public static void init()
    {
        CapabilityManager.INSTANCE.register(Module.class, new EmptyCapStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IPipeNetworkTile.class, new EmptyCapStorage<>(), () -> null);
    }
}