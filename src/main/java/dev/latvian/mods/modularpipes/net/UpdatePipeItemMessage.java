package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.util.PipeItem;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class UpdatePipeItemMessage extends BaseS2CMessage {
	private final PipeItem item;

	public UpdatePipeItemMessage(PipeItem i) {
		item = i;
	}

	public UpdatePipeItemMessage(FriendlyByteBuf buf) {
		item = new PipeItem(buf);
	}

	@Override
	public MessageType getType() {
		return ModularPipesNet.UPDATE_PIPE_ITEM;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		item.write(buf);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ModularPipes.PROXY.updatePipeItem(item);
	}
}