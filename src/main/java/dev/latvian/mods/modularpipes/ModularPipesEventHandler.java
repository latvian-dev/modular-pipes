package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.block.entity.PipeNetwork;
import net.minecraft.resources.ResourceLocation;
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
	private static final ResourceLocation WORLD_CAP_ID = new ResourceLocation(ModularPipes.MOD_ID, "pipe_network");

	@SubscribeEvent
	public static void attachLevelCap(AttachCapabilitiesEvent<Level> event) {
		event.addCapability(WORLD_CAP_ID, new PipeNetwork(event.getObject()));
	}

	@SubscribeEvent
	public static void tickServerWorld(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			PipeNetwork network = PipeNetwork.get(event.world);

			if (network != null) {
				network.tick();
			}
		}
	}
}