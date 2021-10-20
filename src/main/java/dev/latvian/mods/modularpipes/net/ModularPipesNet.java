package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import me.shedaniel.architectury.networking.simple.MessageType;
import me.shedaniel.architectury.networking.simple.SimpleNetworkManager;

/**
 * @author LatvianModder
 */
public interface ModularPipesNet {
	SimpleNetworkManager NET = SimpleNetworkManager.create(ModularPipes.MOD_ID);

	MessageType PARTICLE = NET.registerS2C("particle", ParticleMessage::new);
	MessageType UPDATE_PIPE_ITEM = NET.registerS2C("update_pipe_item", UpdatePipeItemMessage::new);
	MessageType REMOVE_PIPE_ITEM = NET.registerS2C("remove_pipe_item", RemovePipeItemMessage::new);

	static void init() {
	}
}