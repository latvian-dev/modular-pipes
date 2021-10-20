package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.client.ClientPipeNetwork;
import dev.latvian.mods.modularpipes.util.PipeNetwork;
import dev.latvian.mods.modularpipes.util.ServerPipeNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
public class ModularPipesEventHandler {
	private static final ResourceLocation PIPE_NETWORK_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	@SubscribeEvent
	public static void attachLevelCap(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel) {
			event.addCapability(PIPE_NETWORK_CAP_ID, new ServerPipeNetwork(event.getObject()));
		} else {
			event.addCapability(PIPE_NETWORK_CAP_ID, new ClientPipeNetwork(event.getObject()));
		}
	}

	@SubscribeEvent
	public static void tickServerWorld(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END && !event.world.isClientSide()) {
			PipeNetwork network = PipeNetwork.get(event.world);

			if (network instanceof ServerPipeNetwork) {
				network.tick();
			}
		}
	}
}