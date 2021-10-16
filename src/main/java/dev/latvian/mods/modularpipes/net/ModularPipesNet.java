package dev.latvian.mods.modularpipes.net;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.latvian.mods.modularpipes.ModularPipes;

/**
 * @author LatvianModder
 */
public interface ModularPipesNet {
	SimpleNetworkManager NET = SimpleNetworkManager.create(ModularPipes.MOD_ID);

	MessageType PARTICLE = NET.registerS2C("particle", ParticleMessage::new);

	static void init() {
	}
}