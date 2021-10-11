package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * @author LatvianModder
 */
public class ModularPipesNet {
	public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ModularPipes.MOD_ID, "channel"),
			() -> "1.0.0",
			s -> s.startsWith("1"),
			s -> s.startsWith("1")
	);

	public static void init() {
		NET.registerMessage(0, SendPaintMessage.class, SendPaintMessage::toBytes, SendPaintMessage::new, (msg, ctx) -> msg.onMessage(ctx.get()));
		NET.registerMessage(1, ParticleMessage.class, ParticleMessage::toBytes, ParticleMessage::new, (msg, ctx) -> msg.onMessage(ctx.get()));
	}
}