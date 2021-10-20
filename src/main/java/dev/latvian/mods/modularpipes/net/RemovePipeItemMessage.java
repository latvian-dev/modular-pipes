package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class RemovePipeItemMessage extends BaseS2CMessage {
	private final long id;

	public RemovePipeItemMessage(long i) {
		id = i;
	}

	public RemovePipeItemMessage(FriendlyByteBuf buf) {
		id = buf.readVarLong();
	}

	@Override
	public MessageType getType() {
		return ModularPipesNet.REMOVE_PIPE_ITEM;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarLong(id);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ModularPipes.PROXY.removePipeItem(id);
	}
}