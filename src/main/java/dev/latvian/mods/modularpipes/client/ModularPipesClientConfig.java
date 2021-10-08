package dev.latvian.mods.modularpipes.client;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;

/**
 * @author LatvianModder
 */
//@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Side.CLIENT)
//@Config(modid = "modularpipes_client", category = "", name = "../local/client/modularpipes")
public class ModularPipesClientConfig {
	//	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General {
		//		@Config.RangeDouble(min = 0, max = 1000)
		public double item_render_distance = 90D;

		public boolean item_particles = true;

		public boolean add_all_tanks = false;
	}

	public static void sync() {
		//		ConfigManager.sync("modularpipes_client", Config.Type.INSTANCE);
	}

	//	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals("modularpipes_client")) {
			sync();
		}
	}
}