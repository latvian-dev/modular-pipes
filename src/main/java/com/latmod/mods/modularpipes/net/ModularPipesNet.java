package com.latmod.mods.modularpipes.net;

import com.latmod.mods.modularpipes.ModularPipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * @author LatvianModder
 */
public class ModularPipesNet
{
	public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ModularPipes.MOD_ID, "channel"),
			() -> "1.0.0",
			s -> s.startsWith("1"),
			s -> s.startsWith("1")
	);

	public static void init()
	{
		NET.registerMessage(0, MessageSendPaint.class, MessageSendPaint::toBytes, MessageSendPaint::new, (msg, ctx) -> msg.onMessage(ctx.get()));
		NET.registerMessage(1, MessageParticle.class, MessageParticle::toBytes, MessageParticle::new, (msg, ctx) -> msg.onMessage(ctx.get()));
	}
}