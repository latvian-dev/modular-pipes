package dev.latvian.mods.modularpipes.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author LatvianModder
 */
public class SendPaintMessage {
	public final int paint;

	public SendPaintMessage(FriendlyByteBuf buf) {
		paint = buf.readInt();
	}

	public SendPaintMessage(int p) {
		paint = p;
	}

	public void onMessage(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();

		//		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
		//			if (player.openContainer instanceof ContainerPainter)
		//			{
		//				ItemPainter.setPaint(((ContainerPainter) player.openContainer).stack, message.paint);
		//			}
		//		});
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(paint);
	}

}