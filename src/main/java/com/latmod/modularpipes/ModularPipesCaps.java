package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.lib.EmptyCapStorage;
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

	public static void init()
	{
		CapabilityManager.INSTANCE.register(Module.class, new EmptyCapStorage<>(), () -> null);
	}
}